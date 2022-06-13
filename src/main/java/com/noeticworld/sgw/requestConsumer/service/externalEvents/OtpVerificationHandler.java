package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.LoginEntity;
import com.noeticworld.sgw.requestConsumer.entities.OtpRecordsEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.LoginRepository;
import com.noeticworld.sgw.requestConsumer.repository.OtpRecordRepository;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.MtClient;
import com.noeticworld.sgw.util.MtProperties;
import com.noeticworld.sgw.util.RequestProperties;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OtpVerificationHandler implements RequestEventHandler {

    Logger logger = LoggerFactory.getLogger(OtpVerificationHandler.class.getName());

    @Autowired
    ConfigurationDataManagerService dataManagerService;
    @Autowired
    OtpRecordRepository otpRecordRepository;
    @Autowired
    MtClient mtClient;
    @Autowired
    LoginRepository loginRepository;

    @Override
    public void handle(RequestProperties requestProperties) throws URISyntaxException {
        // Do not send OTP to this MSISDN
        if (isNotSentOTPToThisMsisdn(requestProperties)) return;
        Random random = new Random();
        Integer otpNumber = 1000 + random.nextInt(900);

        if (requestProperties.getMsisdn() == 923222200051l) {
            otpNumber = 1234;
        }
        MtProperties mtProperties = new MtProperties();
        VendorPlansEntity vendorPlansEntity = dataManagerService.getVendorPlans(requestProperties.getVendorPlanId());
        System.out.println("vendorPlansEntity.getPlanName()" + vendorPlansEntity.getPlanName() + requestProperties.getVendorPlanId() + " | OTP Number" + otpNumber);


        if(vendorPlansEntity.getOperatorId()==1){
            sendOTP(requestProperties.getMsisdn());
        }
        else {
            // MtMessageSettingsEntity mtMessageSettingsEntity = dataManagerService.getMtMessageSetting(vendorPlansEntity.getId());
            String message = dataManagerService.getMtMessage(vendorPlansEntity.getPlanName() + "_otp").getMsgText();
            String finalMessage = message.replaceAll("&otp", otpNumber.toString());
            mtProperties.setData(finalMessage);
            mtProperties.setMsisdn(Long.toString(requestProperties.getMsisdn()));
            mtProperties.setShortCode("3444");
            mtProperties.setPassword("g@m3now");
            mtProperties.setUsername("gamenow@noetic");
            mtProperties.setServiceId("1061");
            try {
                mtClient.sendMt(mtProperties);
            } catch (Exception e) {
                logger.info("CONSUMER SERVICE | OTPVERIFICATIONHANDLER CLASS | EXCEPTION CAUGHT | " + e.getCause());
            }
            saveOtpRecords(mtProperties, otpNumber, vendorPlansEntity.getId());

        System.out.println("Saving Jazz Msisdn In Login Table : " + " Vendor Plan id : " + requestProperties.getVendorPlanId() );
        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setMsisdn(requestProperties.getMsisdn());
        loginEntity.setUpdateddate(Timestamp.valueOf(LocalDateTime.now()));
        loginEntity.setTrackingId(requestProperties.getTrackerId());
        loginEntity.setCode(otpNumber);
        loginRepository.save(loginEntity);
        }
    }

    private boolean isNotSentOTPToThisMsisdn(RequestProperties requestProperties) {
        List<Long> blockedOTPs = Arrays.asList(923150880379l);
        return blockedOTPs.stream().anyMatch(msisdn -> requestProperties.getMsisdn() == msisdn);
    }

    public void saveOtpRecords(MtProperties mtProperties, Integer otpNumber, long vendorPlanId) {
        OtpRecordsEntity otpRecordsEntity = new OtpRecordsEntity();
        otpRecordsEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        otpRecordsEntity.setMsisdn(Long.parseLong(mtProperties.getMsisdn()));
        otpRecordsEntity.setOtpNumber(otpNumber);
        otpRecordsEntity.setVendorPlanId(vendorPlanId);
        otpRecordRepository.save(otpRecordsEntity);
        logger.info("CONSUMER SERVICE | OTPVERIFICATIONHANDLER CLASS | OTP REOCRDS SAVED");
    }





    //send otp for jazz new
    public String sendOTP(long msisdn) throws URISyntaxException {
        RestTemplate restTemplate=new RestTemplate();
        String param1="jnhuuu58sdf",param2="android",param3="",identifier="";
        String body="{" +
                "\"Identifier\":"+"\""+msisdn+"\","+
                "\"param1\":" +"\"asdjfhjs\","+
                "\"param2\":" +"\"android \","+
                "\"param3\":" +"\"\""+
                "}";
        System.out.println(body);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/json");
        headers.set("Connection","keep-alive");
        headers.set("Authorization","Bearer c4e6cfb8-f47a-38ab-9da8-d4b99fdb8804");
        headers.set("Channel","test-channel");
        HttpEntity<Map<String, Object>> entity = new HttpEntity(body, headers);
        ResponseEntity<String> str= restTemplate.postForEntity(new URI("https://apimtest.jazz.com.pk:8282/auth/sendOTP"),entity,String.class);
        JSONObject json = new JSONObject(str.getBody());
        logger.info(str.getStatusCode() + " " + str.getBody()+" msisdn: "+msisdn);
        return json.getString("msg");
    }














}
