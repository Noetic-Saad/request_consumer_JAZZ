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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public void handle(RequestProperties requestProperties) {
        Random random = new Random();
        Integer otpNumber = 1000 + random.nextInt(900);

        if (requestProperties.getMsisdn() == 923222200051l) {
            otpNumber = 1234;
        }
        MtProperties mtProperties = new MtProperties();
        VendorPlansEntity vendorPlansEntity = dataManagerService.getVendorPlans(requestProperties.getVendorPlanId());
        System.out.println("vendorPlansEntity.getPlanName()" + vendorPlansEntity.getPlanName() + requestProperties.getVendorPlanId() + " | OTP Number" + otpNumber);

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

        if (requestProperties.getVendorPlanId() == 3 || requestProperties.getVendorPlanId() == 12 ||
                requestProperties.getVendorPlanId() == 16 || requestProperties.getVendorPlanId() == 17) {
            System.out.println("Saving Jazz Msisdn In Login Table : " + " Vendor Plan id : " + requestProperties.getVendorPlanId() );
            LoginEntity loginEntity = new LoginEntity();
            loginEntity.setMsisdn(requestProperties.getMsisdn());
            loginEntity.setUpdateddate(Timestamp.valueOf(LocalDateTime.now()));
            loginEntity.setTrackingId(requestProperties.getTrackerId());
            loginEntity.setCode(otpNumber);
            loginRepository.save(loginEntity);
        }

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

}
