package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.RequestEventsEntity;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.entities.SubscriptionSettingEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsEntity;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.RequestHandlerManager;
import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestProcessorService {

    @Autowired private RequestHandlerManager requestHandlerManager;
    @Autowired private ConfigurationDataManagerService configurationDataManagerService;

    public void process(CustomMessage customMessage) {

        SubscriptionSettingEntity subscriptionSetting = configurationDataManagerService.getSubscriptionEntity(Long.parseLong(customMessage.getVendorPlanId()));
        if(!subscriptionSetting.isActive()) {
            //TODO log
            System.out.println("no subscription setting available. Request won't fulfill.");
        }

        requestHandlerManager.manage(customMessage);
        System.out.println("<<<<<<<<<<<<<<<< Request Processed >>>>>>>>>>>>>>>");
    }
}
