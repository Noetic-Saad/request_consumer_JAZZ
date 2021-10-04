package com.noeticworld.sgw.requestConsumer.controllers;

import com.google.gson.Gson;
import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.SubscriptionEventHandler;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.UnsubscriptionEventHandler;
import com.noeticworld.sgw.util.FiegnResponse;
import com.noeticworld.sgw.util.RequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class EDAResponseController {

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    SubscriptionEventHandler subscriptionEventHandler;
    @Autowired
    UnsubscriptionEventHandler unsubscriptionEventHandler;

    @GetMapping("/test")
    public String test() {
        System.out.println("Consumer test");
        return "consumer test";
    }

    @PostMapping("/process-user")
    public void userAcquisitionProcessForEdaRequest(@RequestBody Map<String, ?> requestMap) {
        FiegnResponse fiegnResponse = new Gson().fromJson(requestMap.get("chargingResponse").toString(), FiegnResponse.class);
        long msisdn = (long) requestMap.get("msisdn");

        UsersEntity user = usersRepository.findByMsisdn(msisdn);
        System.out.println("User --- " + user);
        RequestProperties requestProperties = createRequestPropertiesForEDA(user, fiegnResponse);
        System.out.println("Request properties --- " + requestProperties);
        subscriptionEventHandler.processUserForEDA(requestProperties, user);
    }

    @PostMapping("/unsub-user")
    public void unsubUserForEdaRequest(@RequestBody Map<String, ?> requestMap) {
        String correlationId = (String) requestMap.get("correlationId");
        long msisdn = (long) requestMap.get("msisdn");

        RequestProperties requestProperties = new RequestProperties();
        requestProperties.setMsisdn(msisdn);
        requestProperties.setCorrelationId(correlationId);
        requestProperties.setFromEDA(true);
        requestProperties.setRequestAction("Unsb01");
        unsubscriptionEventHandler.handle(requestProperties);
    }

    private RequestProperties createRequestPropertiesForEDA(UsersEntity user, FiegnResponse fiegnResponse) {
        RequestProperties requestProperties = new RequestProperties();
        requestProperties.setVendorPlanId(user.getVendorPlanId());
        requestProperties.setMsisdn(user.getMsisdn());
        requestProperties.setRequestAction(null);
        requestProperties.setCorrelationId(fiegnResponse.getCorrelationId());
        requestProperties.setTrackerId(user.getTrackerId());
        requestProperties.setOriginDateTime(new Date());
        requestProperties.setFromEDA(true);
        requestProperties.setFiegnResponse(fiegnResponse);


        return requestProperties;
    }
}


