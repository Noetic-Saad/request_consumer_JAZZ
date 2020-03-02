package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.VendorPlanRepository;
import com.noeticworld.sgw.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillingService {

    @Autowired private VendorPlanRepository vendorPlanRepository;
    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired private BillingClient billingClient;

    public FiegnResponse charge(RequestProperties requestProperties) {

        VendorPlansEntity vendorPlansEntity = dataService.getVendorPlans(requestProperties.getVendorPlanId());

        ChargeRequestProperties chargeRequestProperties = new ChargeRequestProperties();
        chargeRequestProperties.setOperatorId(vendorPlansEntity.getOperatorId());//TODO OpertorID using static id for testing //Comment By Rizwan
        chargeRequestProperties.setCorrelationId(requestProperties.getCorrelationId());
        chargeRequestProperties.setMsisdn(requestProperties.getMsisdn());
        chargeRequestProperties.setOriginDateTime(requestProperties.getOriginDateTime());
        chargeRequestProperties.setVendorPlanId((int) requestProperties.getVendorPlanId());
        chargeRequestProperties.setShortcode("3444"); //TODO shortcode??
        if (dataService.isTestMsisdn(requestProperties.getMsisdn())){
            chargeRequestProperties.setChargingAmount(1.0);
        }else{
            chargeRequestProperties.setChargingAmount(vendorPlansEntity.getPricePoint());
        }

        chargeRequestProperties.setIsRenewal(0);
        // TODO Comment By Rizwan, Added a new Class FiegnResponse
        FiegnResponse fiegnResponse = billingClient.charge(chargeRequestProperties);
        return fiegnResponse;

    }
}
