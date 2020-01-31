package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionSettingRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.requestConsumer.service.BillingService;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.requestConsumer.service.MtService;
import com.noeticworld.sgw.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Service
@Transactional
public class SubscriptionEventHandler implements RequestEventHandler {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    MtService mtService;
    @Autowired
    private VendorRequestRepository requestRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private SubscriptionSettingRepository subscriptionSettingRepository;
    @Autowired
    private BillingService billingService;
    @Autowired
    private ConfigurationDataManagerService dataService;

    private static final String JAZZ_MSG = "Dear Customer, you are successfully subscribed to Gamenow @PKR5+tax per day. To unsubscribe, go to http://bit.ly/2s7au8P";

    @Override
    public void handle(RequestProperties requestProperties) {

        //get a user either existing or new
        UsersEntity _user = usersRepository.findByMsisdnAndVendorPlanId(
                requestProperties.getMsisdn(), requestProperties.getVendorPlanId());

        boolean exisingUser = true;
        if (_user == null) {
            exisingUser = false;
            _user = registerNewUser(requestProperties);
        }

        if (exisingUser) {
            UsersStatusEntity _usersStatusEntity = userStatusRepository.
                    findTopByUserIdAndVendorPlanIdOrderByIdDesc(
                            _user.getId(), requestProperties.getVendorPlanId());
            if(_usersStatusEntity.getStatusId()==dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.USER_IS_BLOCKED), ResponseTypeConstants.USER_IS_BLOCKED, requestProperties.getCorrelationId());
            }else {
                if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.UNSUBSCRIBED)) {

                    if (_usersStatusEntity == null ||
                            _usersStatusEntity.getExpiryDatetime() == null ||
                            _usersStatusEntity.getExpiryDatetime().
                                    before(new Timestamp(System.currentTimeMillis()))) {
                        processUserRequest(requestProperties, _user);
                    } else {
                        createResponse(ResponseTypeConstants.ALREAD_SUBSCRIBED_MSG, ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
                    }
                } else {
                    createResponse(ResponseTypeConstants.ALREAD_SUBSCRIBED_MSG, ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
                }
            }
        } else {
            processUserRequest(requestProperties,_user);
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
        VendorPlansEntity entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
        usersStatusEntity.setCdate(Timestamp.from(Instant.now()));
        usersStatusEntity.setStatusId(dataService.getUserStatusTypeId(userStatusType));
        usersStatusEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        SubscriptionSettingEntity subscriptionSettingEntity = dataService.getSubscriptionSetting(entity.getId());
        String[] expiryTime = subscriptionSettingEntity.getExpiryTime().split(":");
        int hours = Integer.parseInt(expiryTime[0]);
        int minutes = Integer.parseInt(expiryTime[1]);
        usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(entity.getSubCycle()), LocalTime.of(hours,minutes))));
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);
        updateUserStatus(_user,usersStatusEntity.getId());
        userStatusRepository.flush();
        return usersStatusEntity;
    }

    private void processUserRequest(RequestProperties requestProperties,UsersEntity _user){
        FiegnResponse fiegnResponse = billingService.charge(requestProperties);
        VendorPlansEntity entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
        if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)) {
            if(entity.getMtResponse()==1) {
                mtService.sendSubMt(requestProperties.getMsisdn(), entity);
            }
            createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.INSUFFICIENT_BALANCE)) {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.INSUFFICIENT_BALANCE, requestProperties.getCorrelationId());
        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.ALREADY_SUBSCRIBED)) {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.UNAUTHORIZED_REQUEST)) {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.UNAUTHORIZED_REQUEST, requestProperties.getCorrelationId());
        } else {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.OTHER_ERROR, requestProperties.getCorrelationId());
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        entity.setCdatetime(new Timestamp(new Date().getTime()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        requestRepository.save(entity);
    }

    private void updateUserStatus(UsersEntity user, long userStatusId) {
        user.setUserStatusId((int) userStatusId);
        usersRepository.save(user);
    }
}
