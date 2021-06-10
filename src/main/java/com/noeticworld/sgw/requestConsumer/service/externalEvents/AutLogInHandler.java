package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class AutLogInHandler implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(AutLogInHandler.class.getName());

    @Autowired
    OtpRecordRepository otpRecordRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    UserStatusRepository userStatusRepository;
    @Autowired
    ConfigurationDataManagerService dataService;
    @Autowired
    VendorRequestRepository requestRepository;
    @Autowired
    LogInRecordRepository logInRecordRepository;

    @Override
    public void handle(RequestProperties requestProperties) {

        if (requestProperties.isOtp()) {
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findtoprecord(requestProperties.getMsisdn());
            log.info("AUTOLOGINHANDLER CLASS| OTP RECORD FOUND IN DB IS " + otpRecordsEntity.getOtpNumber());
            if (otpRecordsEntity != null &&
                    otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                processLogInRequest(requestProperties);
            } else {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
        } else {
            processLogInRequest(requestProperties);
        }
    }

    private void processLogInRequest(RequestProperties requestProperties) {

        UsersEntity usersEntity = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        if (usersEntity == null || usersEntity.getUserStatusId() == null) {
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
            return;
        }
        UsersStatusEntity statusEntity = null;
        if (usersEntity.getUserStatusId() == null) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            return;
        }
        statusEntity = userStatusRepository.findTopById(usersEntity.getUserStatusId());
        System.out.println(statusEntity.getStatusId());
        if (statusEntity == null || statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.RENEWALUNSUB)) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS BLOCOKED");

            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)
                && statusEntity.getExpiryDatetime().toLocalDateTime().isAfter(LocalDateTime.now())) {
            System.out.println(statusEntity.getStatusId());
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS VALID USER");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.VALID), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties, usersEntity.getVendorPlanId());
        } else {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        System.out.println("CORREALATIONID || " + correlationId);
        VendorRequestsStateEntity entity = null;
        entity = requestRepository.findByCorrelationid(correlationId);
        boolean isNull = true;
        if (entity == null) {
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
        entity.setDescription(desc);
        requestRepository.save(entity);
    }

    private void saveLogInRecord(RequestProperties requestProperties, long vendorPlanId) {
        log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | AUTO_LOGIN | SESSION ID" + requestProperties.getSessionId());
        log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | AUTO_LOGIN | SESSION ID" + requestProperties.getRemoteServerIp());
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
