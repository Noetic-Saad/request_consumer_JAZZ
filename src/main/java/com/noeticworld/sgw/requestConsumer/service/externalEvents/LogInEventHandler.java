package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import com.noeticworld.sgw.util.ZongBalanceCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LogInEventHandler implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(LogInEventHandler.class.getName());

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
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findTopByMsisdnAndOtpNumber(requestProperties.getMsisdn(), (int) requestProperties.getOtpNumber());
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
        log.info("Entering Function ProcessLoginRequest"+requestProperties.getMsisdn());
        UsersEntity usersEntity = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        if(usersEntity==null || usersEntity.getUserStatusId() == null){
            log.info("****usersEntity==null");
            subscriptionEventHandler.handleSubRequest(requestProperties);
            return;
        }
        UsersStatusEntity statusEntity = null;
        if(usersEntity.getUserStatusId()==null){
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN "+requestProperties.getMsisdn()+" SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
            return;
        }
        statusEntity = userStatusRepository.findTopById(usersEntity.getUserStatusId());
        if(statusEntity == null || statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.RENEWALUNSUB)){
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN "+requestProperties.getMsisdn()+" SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN "+requestProperties.getMsisdn()+" IS BLOCOKED");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
        }
        //Code Added By Habib Ur Rehman 5/11/2020 Added Status Free Trial If user free trial doesn't expire he will be able to login
 /*       else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)
                && statusEntity.getExpiryDatetime().toLocalDateTime().isAfter(LocalDateTime.now()) ||statusEntity.getFreeTrialExpiry().toLocalDateTime().isBefore(LocalDateTime.now())) {
           */
                else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)
                    && statusEntity.getExpiryDatetime().toLocalDateTime().isAfter(LocalDateTime.now()) ) {

                log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN "+requestProperties.getMsisdn()+" IS VALID USER");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.VALID), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties,usersEntity.getVendorPlanId());
        }
                else if(statusEntity.getFreeTrialExpiry()!=null)
        {
            if(statusEntity.getFreeTrialExpiry().toLocalDateTime().isBefore(LocalDateTime.now())) {
            log.info("Successfull Login through Free Trial ");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.VALID), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties, usersEntity.getVendorPlanId());
        }
        }
                    else {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN "+requestProperties.getMsisdn()+" SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        System.out.println("CORREALATIONID || "+correlationId);
        VendorRequestsStateEntity entity = null;
        entity  = requestRepository.findByCorrelationid(correlationId);
        boolean isNull = true;
        if(entity==null){
            while (isNull){
                entity  = requestRepository.findByCorrelationid(correlationId);
                if(entity!=null){
                    isNull = false;
                }
            }
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
