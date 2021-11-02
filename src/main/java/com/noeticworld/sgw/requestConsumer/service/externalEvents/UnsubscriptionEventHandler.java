package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.MsisdnCorrelationsRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.requestConsumer.service.MtService;
import com.noeticworld.sgw.util.RequestActionCodeConstants;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UnsubscriptionEventHandler implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(UnsubscriptionEventHandler.class.getName());

    @Autowired
    MsisdnCorrelationsRepository msisdnCorrelationsRepository;
    @Autowired
    MtService mtService;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private VendorRequestRepository requestRepository;
    @Autowired
    private ConfigurationDataManagerService dataService;

    private boolean isMsisdnWhiteListedForDBSS(RequestProperties requestProperties) {
        List<Long> whiteListedEDAsMSISDN = Arrays.asList(923015195540l, 923080144278l);
        return whiteListedEDAsMSISDN.stream().anyMatch(msisdn -> msisdn == requestProperties.getMsisdn());
    }

    @Override
    public void handle(RequestProperties requestProperties) {
        // Msisdn should be white listed and request should not be from EDA
        if (isMsisdnWhiteListedForDBSS(requestProperties) && !requestProperties.isFromEDA()) {
            createMsisdnCorrelation(requestProperties);

            // ***** Create deactivation route in DBSS service and call BSS API and get request from EDA
            HttpResponse<String> response =
                    Unirest.get("http://192.168.127.58:10001/dbss/product-deactivation/" + requestProperties.getMsisdn()).asString();

            log.info("UnSubscriptionEventHandler | EDA | " + requestProperties.getMsisdn() + " | " + response.getStatus() +
                    " | " + response.getBody());
            return;
        }

        EventTypesEntity eventTypesEntity = dataService.getRequestEventsEntity(requestProperties.getRequestAction());
        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());

        VendorPlansEntity vendorPlans = null;

        if (_user == null) {
            try {
                log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " NOT FOUND");
            } finally {
                createResponse(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND, requestProperties.getCorrelationId());
            }
        } else {
            vendorPlans = dataService.getVendorPlans(_user.getVendorPlanId());
            String resultCode = "";
            try {
                if (eventTypesEntity.getCode().equals(RequestActionCodeConstants.SUBSCRIPTION_REQUEST_TELCO_INITIATED)) {
                    resultCode = changeUserStatus(_user, vendorPlans.getSubCycle(), dataService.getUserStatusTypeId(UserStatusTypeConstants.TELCOUNSUB));
                } else {
                    resultCode = changeUserStatus(_user, vendorPlans.getSubCycle(), dataService.getUserStatusTypeId(UserStatusTypeConstants.UNSUBSCRIBED));
                }
                log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | UNSUBSCRIBED FROM SERVICE");
            } finally {
                log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | TRYING TO CREAT RESPONSE");
                createResponse(resultCode, requestProperties.getCorrelationId());
            }
            if (vendorPlans.getMtResponse() == 1) {
                mtService.sendUnsubMt(requestProperties.getMsisdn(), vendorPlans);
            }
        }
    }

    private void createMsisdnCorrelation(RequestProperties requestProperties) {
        MsisdnCorrelations msisdnCorrelations = new MsisdnCorrelations();
        msisdnCorrelations.setMsisdn(requestProperties.getMsisdn());
        msisdnCorrelations.setCorrelationId(requestProperties.getCorrelationId());
        msisdnCorrelations.setCdate(Timestamp.from(Instant.now()));
        msisdnCorrelationsRepository.save(msisdnCorrelations);
    }

    private String changeUserStatus(UsersEntity users, Integer subCycleId, Integer statusId) {
        log.info("Statud Id For Msisdn" + users.getMsisdn() + " status_id :" + statusId);
        UsersStatusEntity entity = userStatusRepository.findTopById(users.getUserStatusId());
        if (entity == null) {
            log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | MSISDN " + users.getMsisdn() + " STATUS ENTITY NOT FOUND");
            return ResponseTypeConstants.SUBSCRIBER_NOT_FOUND;
        } else if (entity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)) {
            UsersStatusEntity entity1 = new UsersStatusEntity();
            entity1.setUserId(users.getId());
            entity1.setStatusId(statusId);
            entity1.setVendorPlanId(users.getVendorPlanId());
            entity1.setCdate(new Timestamp(new Date().getTime()));
            entity1.setExpiryDatetime(new Timestamp(new Date().getTime()));
            entity1.setSubCycleId(subCycleId);
            entity1.setAttempts(0);
            long userStatusId = userStatusRepository.save(entity1).getId();
            users.setUserStatusId((int) userStatusId);
            users.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
            usersRepository.save(users);
            return ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL;
        } else if (entity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.RENEWALUNSUB)) {
            UsersStatusEntity entity1 = new UsersStatusEntity();
            entity1.setUserId(users.getId());
            entity1.setStatusId(statusId);
            entity1.setVendorPlanId(users.getVendorPlanId());
            entity1.setCdate(new Timestamp(new Date().getTime()));
            entity1.setExpiryDatetime(new Timestamp(new Date().getTime()));
            entity1.setSubCycleId(subCycleId);
            entity1.setAttempts(0);
            long userStatusId = userStatusRepository.save(entity1).getId();
            users.setUserStatusId((int) userStatusId);
            users.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
            usersRepository.save(users);
            return ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL;
        } else if (entity.getStatusId() != dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)) {
            log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | MSISDN " + users.getMsisdn() + " ALREADY UNSUBSCRIBED");
            return ResponseTypeConstants.ALREADY_UNSUBSCRIBED;
        } else {
            return ResponseTypeConstants.OTHER_ERROR;
        }
    }

    private void createResponse(String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        boolean isNull = true;
        if (entity == null) {
            log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | NULL ENTITY");
            while (isNull) {
                entity = requestRepository.findByCorrelationid(correlationId);
                if (entity != null) {
                    isNull = false;
                }
            }
        }
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        if (resultStatus.equals(ResponseTypeConstants.ALREADY_UNSUBSCRIBED)) {
            entity.setDescription(ResponseTypeConstants.ALREAD_SUBSCRIBED_MSG);
        } else if (resultStatus.equals(ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL)) {
            entity.setDescription(ResponseTypeConstants.UNSUBSCRIBEDFULL_MSG);
        } else if (resultStatus.equals(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)) {
            entity.setDescription(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND_MSG);
        } else {
            entity.setResultStatus(ResponseTypeConstants.OTHER_ERROR);
            entity.setDescription(ResponseTypeConstants.OTHER_ERROR_MSG);
        }
        requestRepository.save(entity);
        log.info("CONSUMER SERVICE | UNSUBSCRIPTIONEVENTHANDLER CLASS | RESPONSE CREATED");
    }
}
