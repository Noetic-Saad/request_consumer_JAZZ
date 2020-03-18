package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.GamesBillingRecordEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.GamesBillingRecordRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorPlanRepository;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.LogInEventHandler;
import com.noeticworld.sgw.util.*;
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

    public FiegnResponse charge(RequestProperties requestProperties) {
        FiegnResponse fiegnResponse = null;

        if(isAlreadyChargedToday(requestProperties.getMsisdn())){
            fiegnResponse.setCode(110);
            //fiegnResponse.setCorrelationId(requestProperties.getCorrelationId());
            fiegnResponse.setMsg("ALREADY SUBSCRIBED");
            return fiegnResponse;
        }else {

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
        if (!entity.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isAlreadyChargedToday(long msisdn) {
        log.info("RENEWAL SERVICE | CHARGING_ CLASS | CHECKING IF ALREADY CHARGED TODAY | "+msisdn);
        Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        Timestamp toDate = Timestamp.valueOf(LocalDate.now().atTime(23,59));
        List<GamesBillingRecordEntity> gamesBillingRecordEntity = gamesBillingRecordsRepository.isAlreadyChargedForToday(msisdn,fromDate,toDate);
        if(gamesBillingRecordEntity.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

}
