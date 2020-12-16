package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.GamesBillingRecordEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.GamesBillingRecordRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.util.BillingClient;
import com.noeticworld.sgw.util.ChargeRequestProperties;
import com.noeticworld.sgw.util.FiegnResponse;
import com.noeticworld.sgw.util.RequestProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BillingService {

    Logger log = LoggerFactory.getLogger(BillingService.class.getName());

    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired private BillingClient billingClient;

    @Autowired private GamesBillingRecordRepository gamesBillingRecordsRepository;
    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatusRepository userStatusRepository;

    public FiegnResponse charge(RequestProperties requestProperties) {
        FiegnResponse fiegnResponse = new FiegnResponse();

        if(isAlreadyChargedToday(requestProperties.getMsisdn())){
            log.info("BILLING SERVICE | CHARGING CLASS | ALREADY CHARGED TODAY | "+requestProperties.getMsisdn());
            fiegnResponse.setCode(110);
            fiegnResponse.setCorrelationId(requestProperties.getCorrelationId());
            fiegnResponse.setMsg("ALREADY SUBSCRIBED");
            return fiegnResponse;
        }
        else if(isFreeTrial(requestProperties.getMsisdn())){
            log.info("BILLING SERVICE | CHARGING CLASS | ALREADY CHARGED TODAY | "+requestProperties.getMsisdn());
            fiegnResponse.setCode(110);
            fiegnResponse.setCorrelationId(requestProperties.getCorrelationId());
            fiegnResponse.setMsg("User Still In Free Trial");
            return fiegnResponse;
        }
        else {

            VendorPlansEntity vendorPlansEntity = dataService.getVendorPlans(requestProperties.getVendorPlanId());

            ChargeRequestProperties chargeRequestProperties = new ChargeRequestProperties();
            chargeRequestProperties.setOperatorId(vendorPlansEntity.getOperatorId());
            chargeRequestProperties.setCorrelationId(requestProperties.getCorrelationId());
            chargeRequestProperties.setMsisdn(requestProperties.getMsisdn());
            chargeRequestProperties.setOriginDateTime(requestProperties.getOriginDateTime());
            chargeRequestProperties.setVendorPlanId((int) requestProperties.getVendorPlanId());
            chargeRequestProperties.setShortcode("3444");
            chargeRequestProperties.setSubCycleId(vendorPlansEntity.getSubCycle());
            if (dataService.isTestMsisdn(requestProperties.getMsisdn())) {
                chargeRequestProperties.setChargingAmount(1.0f);
            } else {
                chargeRequestProperties.setChargingAmount(vendorPlansEntity.getPricePoint());
                chargeRequestProperties.setTaxAmount(vendorPlansEntity.getTaxAmount());
            }
            chargeRequestProperties.setIsRenewal(0);
            // TODO Comment By Rizwan, Added a new Class FiegnResponse
            fiegnResponse = billingClient.charge(chargeRequestProperties);
            return fiegnResponse;
        }

    }

    private boolean isAlreadyChargedFor7Days(long msisdn) {
        LocalDateTime toDate = LocalDateTime.now();
        LocalDateTime fromDate = toDate.minusDays(7);
        List<GamesBillingRecordEntity> entity = gamesBillingRecordsRepository.isAlreadyChargedFor7Days(msisdn, Timestamp.valueOf(fromDate), Timestamp.valueOf(toDate));
        return !entity.isEmpty();
    }

    private boolean isAlreadyChargedToday(long msisdn) {
        log.info("BILLING SERVICE | CHARGING CLASS | CHECKING IF ALREADY CHARGED TODAY | "+msisdn);
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp currenttime=Timestamp.valueOf(LocalDateTime.now().plusDays(1));
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atTime(23,59));
        List<GamesBillingRecordEntity> gamesBillingRecordEntity = gamesBillingRecordsRepository.isAlreadyChargedForToday(msisdn,fromDate,toDate);
        return !gamesBillingRecordEntity.isEmpty();
    }
    private boolean isFreeTrial(long msisdn) {
        log.info("BILLING SERVICE | CHARGING CLASS | CHECKING IF FreeTrial CHARGED TODAY | "+msisdn);
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atTime(23,59));
        UsersEntity _user=usersRepository.findByMsisdn(msisdn);
        List<UsersStatusEntity> entitylist=null;
        if(_user!=null ){
            log.info("User Is Not Null Checking Free Trial");
           entitylist=userStatusRepository.IsFreeTrialUser(fromDate,_user.getId());
           if(entitylist!=null){
               return true;
           }

        }
        return false;
    }
}
