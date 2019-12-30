package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionSettingRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.requestConsumer.service.BillingService;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.FiegnResponse;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class SubscriptionEventHandler implements RequestEventHandler {

    @Autowired private UsersRepository usersRepository;
    @Autowired private VendorRequestRepository requestRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private SubscriptionSettingRepository subscriptionSettingRepository;
    @Autowired private BillingService billingService;
    @Autowired private ConfigurationDataManagerService dataService;

    @Override
    public void handle(RequestProperties requestProperties) {

        //get a user either existing or new
        UsersEntity _user = usersRepository.findByMsisdnAndVendorPlanId(
                requestProperties.getMsisdn(), requestProperties.getVendorPlanId());
        String responseType = ResponseTypeConstants.REQUEST_IN_PROGRESS;
        boolean chargedSuccessful;

        boolean exisingUser = true;
        if (_user == null) {
            exisingUser = false;
            _user = registerNewUser(requestProperties);
        }

        if (exisingUser) {
            //get user status
            UsersStatusEntity _usersStatusEntity = userStatusRepository.
                    findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(
                            _user.getId(), requestProperties.getVendorPlanId(),
                            dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED));

            if (_usersStatusEntity == null ||
                    _usersStatusEntity.getExpiryDatetime().
                            before(new Timestamp(System.currentTimeMillis()))) {
                //TODO Changes Made By Rizwan
                FiegnResponse fiegnResponse = billingService.charge(requestProperties);
                if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)) {
                    createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                    createResponse(fiegnResponse.getMsg(),ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
                } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.INSUFFICIENT_BALANCE)) {
                    createResponse(fiegnResponse.getMsg(),ResponseTypeConstants.INSUFFICIENT_BALANCE, requestProperties.getCorrelationId());
                } else if(fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.ALREADY_SUBSCRIBED)) {
                    createResponse(fiegnResponse.getMsg(),ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
                } else if(fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.UNAUTHORIZED_REQUEST)){
                    createResponse(fiegnResponse.getMsg(),ResponseTypeConstants.UNAUTHORIZED_REQUEST, requestProperties.getCorrelationId());
                }else {
                    createResponse(fiegnResponse.getMsg(),ResponseTypeConstants.OTHER_ERROR, requestProperties.getCorrelationId());
                }
              /*  if(chargedSuccessful) {
                    createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                    createResponse(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
                } else {
                    createResponse(ResponseTypeConstants.INSUFFICIENT_BALANCE, requestProperties.getCorrelationId());
                }
            } else {
                createResponse(ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
            }*/
            }
        }
    }

    private UsersEntity registerNewUser(RequestProperties requestProperties) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setMsisdn(requestProperties.getMsisdn());
        usersEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        usersEntity.setCdate(new Date());
        return usersRepository.save(usersEntity);
    }

    private UsersStatusEntity createUserStatusEntity(RequestProperties requestProperties, UsersEntity _user, String userStatusType) {
        UsersStatusEntity usersStatusEntity = new UsersStatusEntity();
        usersStatusEntity.setCdate(Timestamp.from(Instant.now()));
        usersStatusEntity.setStatusId(dataService.getUserStatusTypeId(userStatusType));
        usersStatusEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);
        userStatusRepository.flush();
        return usersStatusEntity;
    }

    private void createResponse(String desc,String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        entity.setCdatetime(new Timestamp(new Date().getTime()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        requestRepository.save(entity);
    }
}
