package com.noeticworld.sgw.requestConsumer.controllers;

import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.SubscriptionEventHandler;
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

    @GetMapping("/test")
    public void test() {
        System.out.println("Consumer test");
    }

    @PostMapping("/process-user")
    public void userAcquisitionProcessForEdaRequest(@RequestBody Map<String, ?> requestMap) {
        FiegnResponse fiegnResponse = (FiegnResponse) requestMap.get("chargingResponse");
        long msisdn = (long) requestMap.get("msisdn");

        UsersEntity user = usersRepository.findByMsisdn(msisdn);
        RequestProperties requestProperties = createRequestPropertiesForEDA(user, fiegnResponse);
        subscriptionEventHandler.processUserForEDA(requestProperties, user);
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
