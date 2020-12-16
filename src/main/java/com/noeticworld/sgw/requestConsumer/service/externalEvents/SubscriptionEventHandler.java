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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @Autowired private LoginRepository loginRepository;

    @Autowired
    ConfigurationDataManagerService dataManagerService;
    @Autowired
    MtClient mtClient;
    private long otpnumber=0;

    @Override
    public void handle(RequestProperties requestProperties) {
        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());

         if (requestProperties.isOtp()) {

            if(requestProperties.getOtpNumber()==0){
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
                log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | OTP IS INVALID FOR | "+requestProperties.getMsisdn());
                return;
            }
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findtoprecord(requestProperties.getMsisdn());
            log.info("SUBSCRIPTION EVENT HANDLER CLASS | OTP RECORD FOUND IN DB IS "+otpRecordsEntity.getOtpNumber());
            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                otpnumber=requestProperties.getOtpNumber();
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

        VendorPlansEntity entity = null;
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
                     createUserStatusEntityFreeTrial(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
                     saveLogInRecord(requestProperties, entity.getId());
                     List<VendorReportEntity> vendorReportEntity = vendorReportRepository.findByMsisdnAndVenodorPlanId(requestProperties.getMsisdn(), (int) requestProperties.getVendorPlanId());

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
        }
    }


    private UsersEntity registerNewUser(RequestProperties requestProperties,VendorPlansEntity entity) {
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
       Timestamp today=Timestamp.valueOf(LocalDateTime.now());
        UsersStatusEntity usersStatusEntity = new UsersStatusEntity();
        List<UsersStatusEntity> usr=userStatusRepository.IsFreeTrialUser(today,_user.getId());
        if(usr.isEmpty()){
            log.info("No Record Found Adding New User Status");
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
        }
        else {
            log.info("User is in free trial No New Entry Is Required in User Status Table");

        }
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | SUBSCRIBED");
        return usersStatusEntity;
    }

    private UsersStatusEntity createUserStatusEntityFreeTrial(RequestProperties requestProperties, UsersEntity _user, String userStatusType) {
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
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
            usersStatusEntity.setFree_trial(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));

        }else {
            usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(7), LocalTime.of(hours, minutes))));
        }
        usersStatusEntity.setAttempts(1);
        usersStatusEntity.setUserId(_user.getId());
        usersStatusEntity = userStatusRepository.save(usersStatusEntity);
        updateUserStatus(_user, usersStatusEntity.getId(),requestProperties.getVendorPlanId());
        userStatusRepository.flush();
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + requestProperties.getMsisdn() + " | SUBSCRIBED");
        return usersStatusEntity;
    }

    private void processUserRequest(RequestProperties requestProperties, UsersEntity _user) {
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
                createUserStatusEntity(requestProperties, _user, UserStatusTypeConstants.SUBSCRIBED);
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
                createResponse(fiegnResponse.getMsg(), ResponseTypeConstants.SUSBCRIBED_SUCCESSFULL, requestProperties.getCorrelationId());
            }
        } else if (fiegnResponse.getCode() == Integer.parseInt(ResponseTypeConstants.INSUFFICIENT_BALANCE)) {
            //added by habib to send mt message if user doesn't have balance

            MtProperties mtProperties = new MtProperties();
            VendorPlansEntity vendorPlansEntity = dataManagerService.getVendorPlans(requestProperties.getVendorPlanId());
            //MtMessageSettingsEntity mtMessageSettingsEntity = dataManagerService.getMtMessageSetting(vendorPlansEntity.getId());
            log.info("Vendor Plan Name"+vendorPlansEntity.getPlanName() );
            String message ="Aap ka balance is service k liye kam hai, apna account recharge kr k is link se dubara try krain.\n" +
                    "http://bit.ly/2s7au8P";
            log.info("Forwarded Message"+ message);
            mtProperties.setData(message);
            mtProperties.setMsisdn(Long.toString(requestProperties.getMsisdn()));
            mtProperties.setShortCode("3444");
            mtProperties.setPassword("g@m3now");
            mtProperties.setUsername("gamenow@noetic");
            mtProperties.setServiceId("1061");
            try {
                mtClient.sendMt(mtProperties);
            } catch (Exception e) {
                log.info("Subscription SERVICE | SUBSCRIPTIONEVENTHANDLER CLASS | EXCEPTION CAUGHT | " + e.getCause());
            }
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
        log.info("CONSUMER SERVICE | SUBSCIPTIONEVENTHANDLER CLASS | " + correlationId + " | TRYING TO CREATE RESPONSE");
        VendorRequestsStateEntity entity = null;
        boolean isNull = true;
        int i=0;
        if(entity==null){
            while (isNull){
                entity  = requestRepository.findByCorrelationid(correlationId);
                System.out.println("ENTITY IS NULL TAKING TIME");

                if(entity!=null){
                    isNull = false;
                }
                i++;
                if(i>10){
                    isNull=false;
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

    private void updateUserStatus(UsersEntity user, long userStatusId,long vendorPLanId) {
        user.setUserStatusId((int) userStatusId);
        user.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        if(user.getVendorPlanId()!=vendorPLanId){
            user.setVendorPlanId(vendorPLanId);
        }
        usersRepository.save(user);
    }

    private void createVendorReport(RequestProperties requestProperties,int postBackSent,Integer operatorId) {
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
        loginRecordsEntity.setCode(otpnumber);
        logInRecordRepository.save(loginRecordsEntity);
        loginRepository.updateLoginTable(requestProperties.getMsisdn());
      /*  LoginEntity lg= loginRepository.findTopByMsisdn(requestProperties.getMsisdn());
        if(lg!=null){
            lg.setCode(0);
        }*/

    }

}
