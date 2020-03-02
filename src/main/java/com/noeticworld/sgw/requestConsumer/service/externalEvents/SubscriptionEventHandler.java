package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.BillingService;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.requestConsumer.service.MtService;
import com.noeticworld.sgw.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger log = LoggerFactory.getLogger(SubscriptionEventHandler.class.getName());

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
    @Autowired
    private VendorReportRepository vendorReportRepository;
    @Autowired
    private OtpRecordRepository otpRecordRepository;
    @Autowired LogInRecordRepository logInRecordRepository;


    @Override
    public void handle(RequestProperties requestProperties) {

        if (requestProperties.isOtp()) {
            if(requestProperties.getOtpNumber()==0){
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
                return;
            }
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findTopByMsisdnAndVendorPlanIdAndOtpNumber(requestProperties.getMsisdn(), requestProperties.getVendorPlanId(), (int) requestProperties.getOtpNumber());
            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                handleSubRequest(requestProperties);
            } else {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
        }else {
            handleSubRequest(requestProperties);
        }


    }

    public void handleSubRequest(RequestProperties requestProperties) {

        VendorPlansEntity entity = null;
        UsersEntity _user = usersRepository.findByMsisdnAndVendorPlanId(
                requestProperties.getMsisdn(), requestProperties.getVendorPlanId());
        boolean exisingUser = true;
        if (_user == null) {
            exisingUser = false;
            entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());

            _user = registerNewUser(requestProperties,entity);

        }

        if (exisingUser) {
            UsersStatusEntity _usersStatusEntity = userStatusRepository.
                    findTopByUserIdAndVendorPlanIdOrderByIdDesc(
                            _user.getId(), requestProperties.getVendorPlanId());
            if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS BLOCKED OR BLACKLISTED");
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.USER_IS_BLOCKED), ResponseTypeConstants.USER_IS_BLOCKED, requestProperties.getCorrelationId());
            } else {
                if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)) {

                    if (_usersStatusEntity == null ||
                            _usersStatusEntity.getExpiryDatetime() == null ||
                            _usersStatusEntity.getExpiryDatetime().
                                    before(new Timestamp(System.currentTimeMillis()))) {
                        processUserRequest(requestProperties, _user);
                    } else {
                        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS ALREADY SUBSCRIBED");
                        createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.ALREADY_SUBSCRIBED), ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
                    }
                }else {
                    processUserRequest(requestProperties, _user);
                }
            }
        } else {
            processUserRequest(requestProperties, _user);
        }
    }


    private UsersEntity registerNewUser(RequestProperties requestProperties,VendorPlansEntity entity) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setMsisdn(requestProperties.getMsisdn());
        usersEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        usersEntity.setCdate(new Date());
        usersEntity.setOperatorId(Long.valueOf(entity.getOperatorId()));
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
        usersStatusEntity.setSubCycleId(entity.getSubCycle());
        usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays((long) dataService.getSubCycleDays(entity.getSubCycle()).getDays()), LocalTime.of(hours, minutes))));
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);
        updateUserStatus(_user, usersStatusEntity.getId());
        userStatusRepository.flush();
        return usersStatusEntity;
    }

    private void processUserRequest(RequestProperties requestProperties, UsersEntity _user) {
        FiegnResponse fiegnResponse = billingService.charge(requestProperties);
        VendorPlansEntity entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
        if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)) {
            if (entity.getMtResponse() == 1) {
                mtService.sendSubMt(requestProperties.getMsisdn(), entity);
            }
            createVendorReport(requestProperties);
            createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties,entity.getId());
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
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        requestRepository.save(entity);
    }

    private void updateUserStatus(UsersEntity user, long userStatusId) {
        user.setUserStatusId((int) userStatusId);
        usersRepository.save(user);
    }

    private void createVendorReport(RequestProperties requestProperties) {
        VendorReportEntity vendorReportEntity = new VendorReportEntity();
        vendorReportEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        vendorReportEntity.setMsisdn(requestProperties.getMsisdn());
        vendorReportEntity.setVenodorPlanId((int) requestProperties.getVendorPlanId());
        vendorReportEntity.setTrackerId(requestProperties.getTrackerId());
        vendorReportRepository.save(vendorReportEntity);
    }

    private void saveLogInRecord(RequestProperties requestProperties,long vendorPlanId){
        LoginRecordsEntity loginRecordsEntity = new LoginRecordsEntity();
        loginRecordsEntity.setCtime(Timestamp.valueOf(LocalDateTime.now()));
        loginRecordsEntity.setSessionId(requestProperties.getSessionId());
        loginRecordsEntity.setAcitve(false);
        loginRecordsEntity.setRemoteServerIp(requestProperties.getRemoteServerIp());
        loginRecordsEntity.setLocalServerIp(requestProperties.getLocalServerIp());
        loginRecordsEntity.setMsisdn(requestProperties.getMsisdn());
        loginRecordsEntity.setVendorPlanId(vendorPlanId);
        logInRecordRepository.save(loginRecordsEntity);
    }

}
