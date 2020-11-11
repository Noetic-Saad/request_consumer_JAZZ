package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.*;
import com.noeticworld.sgw.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SubscriptionEventHandler implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(SubscriptionEventHandler.class.getName());

    @Autowired private UsersRepository usersRepository;
    @Autowired private MtService mtService;
    @Autowired private VendorRequestRepository requestRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private SubscriptionSettingRepository subscriptionSettingRepository;
    @Autowired private BillingService billingService;
    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired private VendorReportRepository vendorReportRepository;
    @Autowired private OtpRecordRepository otpRecordRepository;
    @Autowired private LogInRecordRepository logInRecordRepository;
    @Autowired private VendorPostBackService vendorPostBackService;
    @Autowired private VendorRequestService vendorRequestService;
    @Autowired
    private LoginRepository loginRepository;


    @Override
    public void handle(RequestProperties requestProperties) {
        log.info("Entering Function handle | SubscriptionEventHandler");
        if (requestProperties.isOtp()) {
            if(requestProperties.getOtpNumber()==0){
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | OTP IS INVALID FOR | "+requestProperties.getMsisdn());
                return;
            }
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findTopByMsisdnAndOtpNumber(requestProperties.getMsisdn(), (int) requestProperties.getOtpNumber());
            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                handleSubRequest(requestProperties);
            } else {
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | OTP IS INVALID FOR | "+requestProperties.getMsisdn());
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
        }else {
            handleSubRequest(requestProperties);
        }


    }

    public void handleSubRequest(RequestProperties requestProperties) {
        log.info("Entering Function handleSubRequest");

        VendorPlansEntity entity = null;
        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());

        boolean exisingUser = true;
        if (_user == null) {
            exisingUser = false;

                if( billingService.checkpostpaidprepaid(requestProperties)==true){
                    log.info("******User Is Postpaid **********");

                }
                else {
                    log.info("******User Is Not Postpaid **********");
                    entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
                    log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | REGISTRING NEW USER");
                    _user = registerNewUser(requestProperties,entity);
                    if (entity.getOperatorId() == dataService.getJazz() || entity.getOperatorId()==dataService.getWarid()) {
                        UsersStatusEntity usersStatusEntity = createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                        //updateUserStatus(_user, _user.getId(),requestProperties.getVendorPlanId());
                        Timestamp Expiredate = Timestamp.valueOf(LocalDate.now().plusDays(2).atTime(23, 59));
                        log.info("Crreated UserStatusEntity For Jazz Only : " + usersStatusEntity.getId()+"Get Mt Response : "+entity.getMtResponse() );

                        createResponse1(dataService.getResultStatusDescription(ResponseTypeConstants.VALID), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());

                        if (entity.getMtResponse() == 1) {
                            mtService.sendSubMt(requestProperties.getMsisdn(), entity);
                        }
                        saveLogInRecord(requestProperties, entity.getId());
                        List<VendorReportEntity> vendorReportEntity = vendorReportRepository.findByMsisdnAndVenodorPlanId(requestProperties.getMsisdn(), (int) requestProperties.getVendorPlanId());

                        if(vendorReportEntity.isEmpty()) {
                            log.info("CALLING VENDOR POSTBACK");
                            if(entity.getId()==2){

                            }
                            else {
                                vendorPostBackService.sendVendorPostBack(entity.getId(), requestProperties.getTrackerId());
                                createVendorReport(requestProperties, 1, _user.getOperatorId().intValue());
                            }
                            }else {
                            createVendorReport(requestProperties,0,_user.getOperatorId().intValue());
                        }
                    }
                }





           // processUserRequest(requestProperties, _user);
          //  userStatusRepository.setUserInfoById(Expiredate,0,_user.getId());
          //  VendorPlansEntity entitys = dataService.getVendorPlans(requestProperties.getVendorPlanId());

        }

        if (exisingUser) {
            UsersStatusEntity _usersStatusEntity = userStatusRepository.findTopById(_user.getId());

            if(_usersStatusEntity == null){
                log.info("Not Saving UserStatusEntity");
         createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);

            }else if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS BLOCKED OR BLACKLISTED");
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.USER_IS_BLOCKED), ResponseTypeConstants.USER_IS_BLOCKED, requestProperties.getCorrelationId());
            } else {
                log.info("Not Saving UserStatusEntity");  createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.ALREADY_SUBSCRIBED), ResponseTypeConstants.ALREADY_SUBSCRIBED, requestProperties.getCorrelationId());
                processUserRequest(requestProperties, _user);
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
         //  processUserRequest(requestProperties, _user);

        }
    }


    private UsersEntity registerNewUser(RequestProperties requestProperties,VendorPlansEntity entity) {
        log.info("Entering Function registerNewUser");
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setMsisdn(requestProperties.getMsisdn());
        usersEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        usersEntity.setCdate(new Date());
        usersEntity.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        usersEntity.setOperatorId(Long.valueOf(entity.getOperatorId()));
        if(requestProperties.isOtp()){
            usersEntity.setIsOtpVerifired(1);
        }else {
            usersEntity.setIsOtpVerifired(0);
        }
        usersEntity.setTrackerId(requestProperties.getTrackerId());
        return usersRepository.save(usersEntity);
    }

    private UsersStatusEntity createUserStatusEntity(RequestProperties requestProperties, UsersEntity _user, String userStatusType) {
        log.info("Entering Function createUserStatusEntity");
        log.info("Saving Record In UserStatus Entiry" + requestProperties.getMsisdn() + " | Setting Expiry :"+Timestamp.valueOf(LocalDate.now().plusDays(2).atTime(23,59)));
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
        if(entity.getSubCycle()==1){
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hours, minutes))));
        }else {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.of(hours, minutes))));
        }
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
       usersStatusEntity.setFreeTrialExpiry(Timestamp.valueOf(LocalDate.now().plusDays(2).atTime(23, 59)));
      //  usersStatusEntity.setFreeTrialExpiry(Timestamp.from(Instant.now()));
        usersStatusEntity.setStatus(1);
        userStatusRepository.save(usersStatusEntity);
        updateUserStatus(_user, usersStatusEntity.getId(),requestProperties.getVendorPlanId());
        userStatusRepository.flush();
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | SUBSCRIBED");
        return usersStatusEntity;
    }

    private void processUserRequest(RequestProperties requestProperties, UsersEntity _user) {
        log.info("Entering Function ProcessUserRequest");
       FiegnResponse fiegnResponse = billingService.charge(requestProperties);

        if(fiegnResponse==null){
            return;
        }
        VendorPlansEntity entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
        if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL) || fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.ALREADY_SUBSCRIBED)) {
            if (entity.getMtResponse() == 1 && fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)) {
                mtService.sendSubMt(requestProperties.getMsisdn(), entity);
            }
            try {
                log.info("Sending to createUserStatusEntity sas");
               // createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                saveLogInRecord(requestProperties, entity.getId());
                List<VendorReportEntity> vendorReportEntity = vendorReportRepository.findByMsisdnAndVenodorPlanId(requestProperties.getMsisdn(), (int) requestProperties.getVendorPlanId());
                if(vendorReportEntity.isEmpty()) {
                    log.info("CALLING VENDOR POSTBACK");
                    vendorPostBackService.sendVendorPostBack(entity.getId(), requestProperties.getTrackerId());
                    createVendorReport(requestProperties,1,_user.getOperatorId().intValue());
                }else {
                    createVendorReport(requestProperties,0,_user.getOperatorId().intValue());
                }
            }finally {
                createResponse1(fiegnResponse.getMsg(), ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            }
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

    private void createResponse1(String desc, String resultStatus, String correlationId) {
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + correlationId + " | TRYING TO CREATE RESPONSE");
        VendorRequestsStateEntity entity = null;
        boolean isNull = true;
        if(entity==null){
            while (isNull){
                entity  = requestRepository.findByCorrelationid(correlationId);
                System.out.println("ENTITY IS NULL TAKING TIME");
                if(entity!=null){
                    isNull = false;
                }
            }
        }
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        VendorRequestsStateEntity vre = requestRepository.save(entity);
        //createResponse("Free Trial", ResponseTypeConstants.ALREADY_SUBSCRIBED, correlationId);

        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + vre.getResultStatus() + " | REQUEST STATE UPDATED");
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS |createResponse " + correlationId + " | TRYING TO CREATE RESPONSE "+resultStatus+" Message :"+desc);
        VendorRequestsStateEntity entity = new VendorRequestsStateEntity();
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        entity.setCorrelationid(correlationId);
        vendorRequestService.saveVendorRequest(entity);
    }

    private void updateUserStatus(UsersEntity user, long userStatusId,long vendorPLanId) {
        user.setUserStatusId((int) userStatusId);
        user.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        if(user.getVendorPlanId()!=vendorPLanId){
            user.setVendorPlanId(vendorPLanId);
        }
        usersRepository.save(user);
    }

    private void createVendorReport(RequestProperties requestProperties,int postBackSent,Integer operatorId) {
        log.info("Entering Method createVendorReport");
        VendorReportEntity vendorReportEntity = new VendorReportEntity();
        vendorReportEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        vendorReportEntity.setMsisdn(requestProperties.getMsisdn());
        vendorReportEntity.setVenodorPlanId((int) requestProperties.getVendorPlanId());
        vendorReportEntity.setTrackerId(requestProperties.getTrackerId());
        vendorReportEntity.setPostbackSent(postBackSent);
        vendorReportEntity.setOperatorId(operatorId);
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
