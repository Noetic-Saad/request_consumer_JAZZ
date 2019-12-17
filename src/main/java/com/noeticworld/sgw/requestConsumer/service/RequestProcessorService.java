package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionSettingEntity;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.RequestHandlerManager;
import com.noeticworld.sgw.util.CustomMessage;
import com.noeticworld.sgw.util.RequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestProcessorService {

    @Autowired private RequestHandlerManager requestHandlerManager;
    @Autowired private ConfigurationDataManagerService configurationDataManagerService;

    public void process(CustomMessage customMessage) {

        SubscriptionSettingEntity subscriptionSetting = configurationDataManagerService.getSubscriptionEntity(Long.parseLong(customMessage.getVendorPlanId()));
        if(subscriptionSetting != null && !subscriptionSetting.isActive()) {
            //TODO log
            System.out.println("no subscription setting available. Request won't fulfill.");
        }
        RequestProperties requestProperties = new RequestProperties(customMessage);
        requestHandlerManager.manage(requestProperties);
        System.out.println("<<<<<<<<<<<<<<<< Request Processed >>>>>>>>>>>>>>>");
    }
}
