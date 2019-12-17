package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.VendorPlanRepository;
import com.noeticworld.sgw.util.BillingClient;
import com.noeticworld.sgw.util.ChargeRequestProperties;
import com.noeticworld.sgw.util.RequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BillingService {

    @Autowired private VendorPlanRepository vendorPlanRepository;
    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired private BillingClient billingClient;

    public boolean charge(RequestProperties requestProperties) {
        if(dataService.isTestMsisdn(requestProperties.getMsisdn()))
            return true;

        VendorPlansEntity vendorPlansEntity = vendorPlanRepository.findById(requestProperties.getVendorPlanId()).orElse(null);
        if(vendorPlansEntity == null)
            return false;

        ChargeRequestProperties chargeRequestProperties = new ChargeRequestProperties();
        chargeRequestProperties.setCorrelationId(requestProperties.getCorrelationId());
        chargeRequestProperties.setMsisdn(requestProperties.getMsisdn());
        chargeRequestProperties.setOriginDateTime(requestProperties.getOriginDateTime());
        chargeRequestProperties.setVendorPlanId(requestProperties.getVendorPlanId());
        chargeRequestProperties.setShortcode("3444"); //TODO shortcode??
        chargeRequestProperties.setChargingAmount(vendorPlansEntity.getPricePoint());

        return billingClient.charge(chargeRequestProperties);
    }
}
