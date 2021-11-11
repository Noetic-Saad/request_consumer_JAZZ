package com.noeticworld.sgw.requestConsumer.controllers;

import com.google.gson.Gson;
import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.SubscriptionEventHandler;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.UnsubscriptionEventHandler;
import com.noeticworld.sgw.util.FiegnResponse;
import com.noeticworld.sgw.util.RequestProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class EDAResponseController {
    Logger log = LoggerFactory.getLogger(EDAResponseController.class.getName());

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    SubscriptionEventHandler subscriptionEventHandler;
    @Autowired
    UnsubscriptionEventHandler unsubscriptionEventHandler;

    @PostMapping("/process-user")
    public void userAcquisitionProcessForEdaRequest(@RequestBody Map<String, ?> requestMap) {
        FiegnResponse fiegnResponse = new Gson().fromJson(requestMap.get("chargingResponse").toString(), FiegnResponse.class);
        long msisdn = (long) requestMap.get("msisdn");

        UsersEntity user = usersRepository.findByMsisdn(msisdn);
        log.info("EDAResponseController | " + msisdn + " | " + user.getId());

        RequestProperties requestProperties = createRequestPropertiesForEDA(user, fiegnResponse);
        log.info("EDAResponseController | " + msisdn + " | " + requestProperties.getCorrelationId());

        subscriptionEventHandler.processUserForEDA(requestProperties, user);
    }

    @PostMapping("/unsub-user")
    public void unsubUserForEdaRequest(@RequestBody Map<String, ?> requestMap) {
        String correlationId = (String) requestMap.get("correlationId");
        long msisdn = (long) requestMap.get("msisdn");

        System.out.println("*********** " + correlationId);
        System.out.println(correlationId == "OP_UNSUB");
        System.out.println(correlationId.equals("OP_UNSUB"));


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


