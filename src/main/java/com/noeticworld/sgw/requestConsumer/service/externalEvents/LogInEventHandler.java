package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import com.noeticworld.sgw.util.ZongBalanceCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class LogInEventHandler implements RequestEventHandler {

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserStatusRepository userStatusRepository;
    @Autowired
    ConfigurationDataManagerService dataService;
    @Autowired
    VendorRequestRepository requestRepository;
    @Autowired
    OtpRecordRepository otpRecordRepository;
    @Autowired
    LogInRecordRepository logInRecordRepository;
    @Autowired SubscriptionEventHandler subscriptionEventHandler;

    @Override
    public void handle(RequestProperties requestProperties)  {

        if (requestProperties.isOtp()) {
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findTopByMsisdnAndVendorPlanIdAndOtpNumber(requestProperties.getMsisdn(), requestProperties.getVendorPlanId(), (int) requestProperties.getOtpNumber());
            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                processLogInRequest(requestProperties);
            } else {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
        } else {
            processLogInRequest(requestProperties);
        }

    }

    private void processLogInRequest(RequestProperties requestProperties) {
        ZongBalanceCheck zongBalanceCheck = new ZongBalanceCheck();
        if(1==1){
            zongBalanceCheck.logIn();
            String query = zongBalanceCheck.balanceQuery(requestProperties.getMsisdn());
            System.out.println(query);
        }
        UsersEntity usersEntity = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        if(usersEntity==null){
            subscriptionEventHandler.handleSubRequest(requestProperties);
            return;
        }
        UsersStatusEntity statusEntity = userStatusRepository.findTopByIdAndVendorPlanId(usersEntity.getUserStatusId(), usersEntity.getVendorPlanId());
        if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
            return;
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)
                && statusEntity.getExpiryDatetime().toLocalDateTime().isAfter(LocalDateTime.now())) {
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.VALID), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties,usersEntity.getVendorPlanId());
        } else {
            subscriptionEventHandler.handleSubRequest(requestProperties);
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        if(entity==null){
            System.out.println("ENTITY IS NULL || ");
        }
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        requestRepository.save(entity);
    }

    private void saveLogInRecord(RequestProperties requestProperties,long vendorPlanId){
        LoginRecordsEntity loginRecordsEntity = new LoginRecordsEntity();
        loginRecordsEntity.setCtime(Timestamp.valueOf(LocalDateTime.now()));
        loginRecordsEntity.setSessionId(requestProperties.getSessionId());
        loginRecordsEntity.setRemoteServerIp(requestProperties.getRemoteServerIp());
        loginRecordsEntity.setAcitve(true);
        loginRecordsEntity.setLocalServerIp(requestProperties.getLocalServerIp());
        loginRecordsEntity.setMsisdn(requestProperties.getMsisdn());
        loginRecordsEntity.setVendorPlanId(vendorPlanId);
        logInRecordRepository.save(loginRecordsEntity);
    }
}
