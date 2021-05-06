package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.BillingService;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.requestConsumer.service.MtService;
import com.noeticworld.sgw.requestConsumer.service.VendorPostBackService;
import com.noeticworld.sgw.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionEventHandler222 implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(SubscriptionEventHandler222.class.getName());
    @Autowired
    ConfigurationDataManagerService dataManagerService;
    @Autowired
    MtClient mtClient;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MtService mtService;
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
    @Autowired
    private LogInRecordRepository logInRecordRepository;
    @Autowired
    private VendorPostBackService vendorPostBackService;
    @Autowired
    private LoginRepository loginRepository;
    private long otpnumber = 0;

    @Override
    public void handle(RequestProperties requestProperties) {
//        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());

        if (requestProperties.isOtp()) {
            if (requestProperties.getOtpNumber() == 0) {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | OTP IS INVALID FOR | " + requestProperties.getMsisdn());
                return;
            }
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findtoprecord(requestProperties.getMsisdn());
            log.info("SUBSCRIPTION EVENT HANDLER CLASS | OTP RECORD FOUND IN DB IS " + otpRecordsEntity.getOtpNumber());
            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                otpnumber = requestProperties.getOtpNumber();
                handleSubRequest(requestProperties);
            } else {
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | OTP IS INVALID FOR | " + requestProperties.getMsisdn());
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
        } else {
            handleSubRequest(requestProperties);
        }


    }

    public void handleSubRequest(RequestProperties requestProperties) {
        //Free Trial Changes
       /* VendorPlansEntity entity = null;
        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        boolean exisingUser = true;
        if (_user == null) {
            exisingUser = false;


            RestTemplate template = new RestTemplate();
            RequestPropertiesCheckBalance rq=new RequestPropertiesCheckBalance();
            rq.setMsisdn(requestProperties.getMsisdn());
            rq.setOperatorId(10);
            rq.setTransactionId("111");
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RequestPropertiesCheckBalance> requestEntity =
                    new HttpEntity<>(rq, headers);
            HttpEntity<AppResponse>  response =
                    template.exchange("http://192.168.127.57:9071/checkbalance", HttpMethod.POST, requestEntity,
                            AppResponse.class);
         if(response==null){
             log.info("Null Response From Api "+response.getBody().getCode() +response.getBody().getMsg());
                }
            else  if(response!=null && response.getBody().getCode()==1000){
                log.info("Jazz Customer from checkbalancedate api"+ response.getBody().getCode() +response.getBody().getMsg());
             entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
             log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | REGISTRING NEW USER "+requestProperties.getVendorPlanId());
             _user = registerNewUser(requestProperties,entity);
             if(requestProperties.getVendorPlanId()==3 ||requestProperties.getVendorPlanId()==12 ||requestProperties.getVendorPlanId()==16) {

                 try {
                     VendorPlansEntity vendorPlans = null;
                     createUserStatusEntityFreeTrial(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                     saveLogInRecord(requestProperties, entity.getId());
                     vendorPlans = dataService.getVendorPlans(_user.getVendorPlanId());
                     List<VendorReportEntity> vendorReportEntity = vendorReportRepository.findByMsisdnAndVenodorPlanId(requestProperties.getMsisdn(), (int) requestProperties.getVendorPlanId());
                     if(vendorPlans.getMtResponse() == 1) {
                         mtService.sendSubMt(requestProperties.getMsisdn(), vendorPlans);
                     }
                     if (vendorReportEntity.isEmpty()) {
                         if (requestProperties.getVendorPlanId() == 3 || requestProperties.getVendorPlanId() == 4 || requestProperties.getVendorPlanId() == 5) {
                             createVendorReport(requestProperties, 1, _user.getOperatorId().intValue());
                         } else {
                             log.info("CALLING VENDOR POSTBACK");
                             vendorPostBackService.sendVendorPostBack(entity.getId(), requestProperties.getTrackerId());
                             createVendorReport(requestProperties, 1, _user.getOperatorId().intValue());
                         }
                     } else {
                         createVendorReport(requestProperties, 0, _user.getOperatorId().intValue());
                     }
                 } finally {
                     createResponse("Subscribe For Free Trial", ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
                 }
             }
             else{
                 processUserRequest(requestProperties,_user);

             }

            }
            else{
                log.info("Other Response"+ response.getBody().getCode() +response.getBody().getMsg());
            }


        }

        if (exisingUser) {
            UsersStatusEntity _usersStatusEntity = userStatusRepository.findTopById(_user.getId());
            if(_usersStatusEntity == null){
                processUserRequest(requestProperties, _user);
            }else if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
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
        }*/


        VendorPlansEntity entity = null;
        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        boolean exisingUser = true;

        if (_user == null) {
            exisingUser = false;
            entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
            log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | REGISTRING NEW USER");
            _user = registerNewUser(requestProperties, entity);

        }

        if (exisingUser) {
            UsersStatusEntity _usersStatusEntity = userStatusRepository.findTopById(_user.getId());
            if (_usersStatusEntity == null) {
                processUserRequest(requestProperties, _user);
            } else if (_usersStatusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
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
                } else {
                    processUserRequest(requestProperties, _user);
                }
            }
        } else {
            processUserRequest(requestProperties, _user);
        }
    }


    private UsersEntity registerNewUser(RequestProperties requestProperties, VendorPlansEntity entity) {
        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setMsisdn(requestProperties.getMsisdn());
        usersEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        usersEntity.setCdate(new Date());
        usersEntity.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        usersEntity.setOperatorId(Long.valueOf(entity.getOperatorId()));
        if (requestProperties.isOtp()) {
            usersEntity.setIsOtpVerifired(1);
        } else {
            usersEntity.setIsOtpVerifired(0);
        }
        usersEntity.setTrackerId(requestProperties.getTrackerId());
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
        if (entity.getSubCycle() == 1) {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hours, minutes))));
        } else {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.of(hours, minutes))));
        }
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);

        updateUserStatus(_user, usersStatusEntity.getId(), requestProperties.getVendorPlanId());

        userStatusRepository.flush();

        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " " +
                "| USER STATUS CREATED");
        return usersStatusEntity;
    }

    /*private UsersStatusEntity createUserStatusEntityFreeTrial(RequestProperties requestProperties, UsersEntity _user, String userStatusType) {
        UsersStatusEntity usersStatusEntity = new UsersStatusEntity();
        VendorPlansEntity entity = dataService.getVendorPlans(requestProperties.getVendorPlanId());
        usersStatusEntity.setCdate(Timestamp.from(Instant.now()));
        usersStatusEntity.setStatusId(dataService.getUserStatusTypeId(userStatusType));
        usersStatusEntity.setVendorPlanId(requestProperties.getVendorPlanId());
        SubscriptionSettingEntity subscriptionSettingEntity = dataService.getSubscriptionSetting(entity.getId());
        // String[] expiryTime = subscriptionSettingEntity.getExpiryTime().split(":");
        //int hours = Integer.parseInt(expiryTime[0]);
        // int minutes = Integer.parseInt(expiryTime[1]);
        usersStatusEntity.setSubCycleId(entity.getSubCycle());
        if (entity.getSubCycle() == 1) {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
            usersStatusEntity.setFree_trial(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));

        } else {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.now().plusDays(7)));
        }
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);
        updateUserStatus(_user, usersStatusEntity.getId(), requestProperties.getVendorPlanId());
        userStatusRepository.flush();
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | SUBSCRIBED");
        return usersStatusEntity;
    }*/

    private void processUserRequest(RequestProperties requestProperties, UsersEntity _user) {
        FiegnResponse fiegnResponse = billingService.charge(requestProperties);
        log.info("********** Sending Request For Charging *******");

        UsersStatusEntity lastUserStatus = null;
        VendorPlansEntity vendorPlansEntity = dataService.getVendorPlans(requestProperties.getVendorPlanId());

        if (fiegnResponse == null) {
            return;
        }

        if (_user.getUserStatusId() != null) {
            lastUserStatus = userStatusRepository.UnsubStatus(_user.getId());
        }


        if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.ALREADY_SUBSCRIBED)) {
            // ALREADY SUBSCRIBED CASE

            String responseTypeConstant = null;

            if (vendorPlansEntity.getOperatorId() == 1) {
                // Create already subscribed response | game now.
                responseTypeConstant = ResponseTypeConstants.ALREADY_SUBSCRIBED;
            } else if (vendorPlansEntity.getOperatorId() == 4) {
                // Create valid response | zong games.
                responseTypeConstant = ResponseTypeConstants.VALID;
            }

            // Send response.
            try {
                createResponse(fiegnResponse.getMsg(), responseTypeConstant, requestProperties.getCorrelationId());
            } catch (Exception e) {
                log.info("Subscription SERVICE | Exception | Creating response | " + e.getCause());
            }

            continueUserSubscriptionProcess(requestProperties, _user, vendorPlansEntity);

        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL)) {
            // SUBSCRIBED SUCCESSFUL

            String message = null;
            boolean isMtAllowed = false;

            try {
                createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
            } catch (Exception e) {
                log.info("Subscription SERVICE | Exception | Creating response | " + e.getCause());
            }

            if (vendorPlansEntity.getOperatorId() == 1) {
                message = dataManagerService.getMtMessage("jazz_sub").getMsgText();

                // Auto generated, need to check this.
                isMtAllowed = lastUserStatus == null || lastUserStatus.getStatusId() != 8;
            } else if (vendorPlansEntity.getOperatorId() == 4) {
                message = dataManagerService.getMtMessage("zong_sub").getMsgText();
                isMtAllowed = true;
            }

            if (isMtAllowed) {
                sendMT(requestProperties, message);
            }

            continueUserSubscriptionProcess(requestProperties, _user, vendorPlansEntity);

        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.INSUFFICIENT_BALANCE)) {
            // INSUFFICIENT BALANCE

            String message = null;
            boolean isMtAllowed = false;

            if (vendorPlansEntity.getOperatorId() == 1) {
                // In case of game now, create free trial response.
                try {
                    createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.FREE_TRIAL_SUBSCRIPTION,
                            requestProperties.getCorrelationId());
                } catch (Exception e) {
                    log.info("Subscription SERVICE | Exception | Creating response | " + e.getCause());
                }

                // Get free trial MT message from DB | Don't send free trial MT because the user is in renewal.
                message = dataManagerService.getMtMessage("jazz_sub_freetrial").getMsgText();

                // This is to check if the user tries to login by himself and msisdn was in renewal. So in this case, we don't
                // want to send free trial MT to the user.
                isMtAllowed = lastUserStatus == null || lastUserStatus.getStatusId() != 8;

                if (isMtAllowed) {
                    sendMT(requestProperties, message);
                }

                try {
                    createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                    saveLogInRecord(requestProperties, vendorPlansEntity.getId());
                } catch (Exception e) {
                    log.info("Subscription SERVICE |  Exception | User status & login updates | " + e.getCause());
                }
            } else if (vendorPlansEntity.getOperatorId() == 4) {
                // In case of Zong games, create free trial as well.
                try {
                    createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.FREE_TRIAL_SUBSCRIPTION,
                            requestProperties.getCorrelationId());
                } catch (Exception e) {
                    log.info("Subscription SERVICE | Exception | Creating response | " + e.getCause());
                }

                // Get zong MT message & send MT.
                message = dataManagerService.getMtMessage("zong_sub_freetrial").getMsgText();

                // Auto generated, need to check this.
                isMtAllowed = lastUserStatus == null || lastUserStatus.getStatusId() != 8;

                if (isMtAllowed) {
                    sendMT(requestProperties, message);
                }

                try {
                    createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                    saveLogInRecord(requestProperties, vendorPlansEntity.getId());
                } catch (Exception e) {
                    log.info("Subscription SERVICE |  Exception | User status & login updates | " + e.getCause());
                }
            }
        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.UNAUTHORIZED_REQUEST)) {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.UNAUTHORIZED_REQUEST, requestProperties.getCorrelationId());
        } else {
            createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.OTHER_ERROR, requestProperties.getCorrelationId());
        }
    }

    private void continueUserSubscriptionProcess(RequestProperties requestProperties, UsersEntity _user, VendorPlansEntity vendorPlansEntity) {
        try {
            createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);

            saveLogInRecord(requestProperties, vendorPlansEntity.getId());

            List<VendorReportEntity> vendorReportEntity = vendorReportRepository.findByMsisdnAndVenodorPlanId(requestProperties.getMsisdn(), (int) requestProperties.getVendorPlanId());

            if (vendorReportEntity.isEmpty()) {
                log.info("CALLING VENDOR POSTBACK");

                vendorPostBackService.sendVendorPostBack(vendorPlansEntity.getId(), requestProperties.getTrackerId());
                createVendorReport(requestProperties, 1, _user.getOperatorId().intValue());
            } else {
                createVendorReport(requestProperties, 0, _user.getOperatorId().intValue());
            }
        } catch (Exception e) {
            log.info("Subscription SERVICE | Exception | User status | login | vendor PB | vendor " +
                    "report" +
                    " | " + e.getCause());
        }
    }

    private void sendMT(RequestProperties requestProperties, String message) {
        MtProperties mtProperties = new MtProperties();
        mtProperties.setData(message);
        mtProperties.setMsisdn(Long.toString(requestProperties.getMsisdn()));
        mtProperties.setShortCode("3444");
        mtProperties.setPassword("g@m3now");
        mtProperties.setUsername("gamenow@noetic");
        mtProperties.setServiceId("1061");

        try {
            mtClient.sendMt(mtProperties);
            mtService.saveMessageRecord(requestProperties.getMsisdn(), message);
        } catch (Exception e) {
            log.info("SubscriptionEventHandler | Subscribe MT Exception | " + e.getCause());
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + correlationId + " | TRYING TO CREATE RESPONSE");
        VendorRequestsStateEntity entity = null;
        boolean isNull = true;
        int i = 0;
        if (entity == null) {
            while (isNull) {
                entity = requestRepository.findByCorrelationid(correlationId);
                System.out.println("ENTITY IS NULL TAKING TIME");

                if (entity != null) {
                    isNull = false;
                }
                i++;
                if (i > 10) {
                    isNull = false;
                }
            }
        }
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        entity.setDescription(desc);
        VendorRequestsStateEntity vre = requestRepository.save(entity);
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + vre.getResultStatus() + " | REQUEST STATE UPDATED");
    }

    private void updateUserStatus(UsersEntity user, long userStatusId, long vendorPLanId) {
        user.setUserStatusId((int) userStatusId);
        user.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        if (user.getVendorPlanId() != vendorPLanId) {
            user.setVendorPlanId(vendorPLanId);
        }
        usersRepository.save(user);
    }

    private void createVendorReport(RequestProperties requestProperties, int postBackSent, Integer operatorId) {
        VendorReportEntity vendorReportEntity = new VendorReportEntity();
        vendorReportEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        vendorReportEntity.setMsisdn(requestProperties.getMsisdn());
        vendorReportEntity.setVenodorPlanId((int) requestProperties.getVendorPlanId());
        vendorReportEntity.setTrackerId(requestProperties.getTrackerId());
        vendorReportEntity.setPostbackSent(postBackSent);
        vendorReportEntity.setOperatorId(operatorId);
        vendorReportRepository.save(vendorReportEntity);
    }

    private void saveLogInRecord(RequestProperties requestProperties, long vendorPlanId) {
        LoginRecordsEntity loginRecordsEntity = new LoginRecordsEntity();
        loginRecordsEntity.setCtime(Timestamp.valueOf(LocalDateTime.now()));

        loginRecordsEntity.setSessionId(requestProperties.getSessionId());
        loginRecordsEntity.setAcitve(false);
        loginRecordsEntity.setRemoteServerIp(requestProperties.getRemoteServerIp());
        loginRecordsEntity.setLocalServerIp(requestProperties.getLocalServerIp());
        loginRecordsEntity.setMsisdn(requestProperties.getMsisdn());
        loginRecordsEntity.setVendorPlanId(vendorPlanId);
        loginRecordsEntity.setCode(otpnumber);
        logInRecordRepository.save(loginRecordsEntity);
        loginRepository.updateLoginTable(requestProperties.getMsisdn());
      /*  LoginEntity lg= loginRepository.findTopByMsisdn(requestProperties.getMsisdn());
        if(lg!=null){
            lg.setCode(0);
        }*/

    }

}