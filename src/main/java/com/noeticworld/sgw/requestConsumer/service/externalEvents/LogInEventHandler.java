package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

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
    @Autowired
    SubscriptionEventHandler subscriptionEventHandler;
    @Autowired
    LoginRepository loginRepository;

    @Override
    public void handle(RequestProperties requestProperties) throws URISyntaxException {
        if (requestProperties.isOtp()) {

            //New jazz
            if(requestProperties.getVendorPlanId()==3){
                log.info("LOGIN EVENT HANDLER CLASS | New OTP API Called "+ " | msisdn:" + requestProperties.getMsisdn());
                String str=verifyOTP(requestProperties.getMsisdn(),requestProperties.getOtpNumber());
                if(str.equals("success")){
                    loginRepository.updateLoginTable(requestProperties.getMsisdn());
                    processLogInRequest(requestProperties);
                }else {
                    createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
                }


            }
            else{
            OtpRecordsEntity otpRecordsEntity = otpRecordRepository.findtoprecord(requestProperties.getMsisdn());
            log.info("LOGIN EVENT HANDLER CLASS | OTP RECORD FOUND IN DB IS " + otpRecordsEntity.getOtpNumber() + " | msisdn:" + requestProperties.getMsisdn());

            if (otpRecordsEntity != null && otpRecordsEntity.getOtpNumber() == requestProperties.getOtpNumber()) {
                loginRepository.updateLoginTable(requestProperties.getMsisdn());
                processLogInRequest(requestProperties);
            } else {
                createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID_OTP), ResponseTypeConstants.INVALID_OTP, requestProperties.getCorrelationId());
            }
            }





        } else {
            UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());

            if (_user != null) {
                log.info("User Already Exist" + " | msisdn:" + requestProperties.getMsisdn());
                UsersStatusEntity user_status_id = userStatusRepository.UnsubStatus(_user.getId());
                UsersEntity user_status = usersRepository.FindByTopMSISDN(_user.getMsisdn());

                if (user_status_id != null) {
                    if (user_status.getUserStatusId() == null) {
                        log.info("*******UNCharged Users************* : " + user_status_id + " | msisdn:" + requestProperties.getMsisdn());
                        createResponse("OTP Required", ResponseTypeConstants.NOTREGISTERED, requestProperties.getCorrelationId());

                    } else if (user_status_id.getStatusId() == 2 || user_status_id.getStatusId() == 5 || user_status_id.getStatusId() == 4) {
                        log.info("*******Unsubscribed Users************* : " + user_status_id + " | msisdn:" + requestProperties.getMsisdn());
                        createResponse("OTP Required", ResponseTypeConstants.NOTREGISTERED, requestProperties.getCorrelationId());

                    } else {
                        log.info("Processing Request Without asking for otp" + " | msisdn:" + requestProperties.getMsisdn());
                        processLogInRequest(requestProperties);
                        // Yahan pr request ati agr user already renewal ma para hua
                        // Tw ab yahan pr 2 conditions bach jati, either user is 1/charged or 8/renewal unsub.
                        // now agr user already subscribed hy or subscription remaining hy tw valid 118 response ana.
                    }
                } else {
                    log.info("User status id was not created | process request for otp" + " | msisdn:" + requestProperties.getMsisdn());
                    createResponse("OTP Required", ResponseTypeConstants.NOTREGISTERED, requestProperties.getCorrelationId());
                }
            } else {
                createResponse("OTP Required", ResponseTypeConstants.NOTREGISTERED, requestProperties.getCorrelationId());
            }

        }

    }

    private void processLogInRequest(RequestProperties requestProperties) {
        UsersEntity usersEntity = usersRepository.findByMsisdn(requestProperties.getMsisdn());

        if (usersEntity == null || usersEntity.getUserStatusId() == null) {
            subscriptionEventHandler.handleSubRequest(requestProperties);
            return;
        }
        UsersStatusEntity statusEntity = null;
        if (usersEntity.getUserStatusId() == null) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
            return;
        }
        statusEntity = userStatusRepository.findTopById(usersEntity.getUserStatusId());
        if (statusEntity == null || statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.RENEWALUNSUB)) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.BLOCKED)) {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS BLOCOKED");
            createResponse(dataService.getResultStatusDescription(ResponseTypeConstants.INVALID), ResponseTypeConstants.INVALID, requestProperties.getCorrelationId());
        } else if (statusEntity.getStatusId() == dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)
                && statusEntity.getExpiryDatetime().toLocalDateTime().isAfter(LocalDateTime.now())) {
            log.info("********User Status Id : " + statusEntity.getId() + " User Status" + statusEntity.getStatusId() + " Expired At" + statusEntity.getExpiryDatetime());

            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | MSISDN " + requestProperties.getMsisdn() + " IS VALID USER");
            createResponse("Valid User", ResponseTypeConstants.VALID, requestProperties.getCorrelationId());
            saveLogInRecord(requestProperties, usersEntity.getVendorPlanId());
        } else {
            log.info("CONSUMER SERVICE | LOGINEVENTHANDLER CLASS | FOR MSISDN " + requestProperties.getMsisdn() + " SENDING SUB REQUEST");
            subscriptionEventHandler.handleSubRequest(requestProperties);
        }
    }

    private void createResponse(String desc, String resultStatus, String correlationId) {
        System.out.println("CORREALATIONID || " + correlationId);
        try {
            VendorRequestsStateEntity entity = null;
            entity = requestRepository.findByCorrelationid(correlationId);
            boolean isNull = true;
            int i = 0;
            if (entity == null) {
                while (isNull) {

                    entity = requestRepository.findByCorrelationid(correlationId);

                    i++;
                    log.error("entity is null trying to create response" + i);
                    if (entity != null) {
                        isNull = false;
                    }
                    if (i == 10) {
                        isNull = false;
                    }
                }
            }
            entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
            entity.setFetched(false);
            entity.setResultStatus(resultStatus);
            entity.setDescription(desc);
            requestRepository.save(entity);
        } catch (Exception ex) {
            log.error("Error In Creating Response" + ex);
        }
    }

    private void saveLogInRecord(RequestProperties requestProperties, long vendorPlanId) {
        LoginRecordsEntity loginRecordsEntity = new LoginRecordsEntity();
        loginRecordsEntity.setCtime(Timestamp.valueOf(LocalDateTime.now()));
        loginRecordsEntity.setSessionId(requestProperties.getSessionId());
        loginRecordsEntity.setRemoteServerIp(requestProperties.getRemoteServerIp());
        loginRecordsEntity.setAcitve(true);
        loginRecordsEntity.setLocalServerIp(requestProperties.getLocalServerIp());
        loginRecordsEntity.setMsisdn(requestProperties.getMsisdn());
        loginRecordsEntity.setVendorPlanId(vendorPlanId);
        logInRecordRepository.save(loginRecordsEntity);
//        loginRepository.updateLoginTable(requestProperties.getMsisdn());
    }


    public String verifyOTP(long msisdn,long otp) throws URISyntaxException {
        RestTemplate restTemplate=new RestTemplate();
        String param1="jnhuuu58sdf",param2="android",param3="",identifier="";
        String body="{" +
                "\"Identifier\":"+"\""+msisdn+"\","+
                "\"OTP\":\""+otp+"\","+
                "\"param1\":" +"\"asdjfhjs\","+
                "\"param2\":" +"\"android \","+
                "\"param3\":" +"\"\""+
                "}";
        System.out.println(body);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("Connection","keep-alive");
        headers.set("Authorization","Bearer b65728a7-da4f-30c3-bc17-98165dd78256");
        headers.set("Channel","test-channel");
        HttpEntity<Map<String, Object>> entity = new HttpEntity(body, headers);
        ResponseEntity<String> str= restTemplate.postForEntity(new URI("https://apimtest.jazz.com.pk:8282/auth/verifyOTP"),entity,String.class);
        JSONObject json = new JSONObject(str.getBody());
        log.info(str.getStatusCode()+" "+str.getBody()+ "msidn: "+msisdn);
        return json.getString("msg");
    }




}
