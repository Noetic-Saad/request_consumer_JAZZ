package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsEntity;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SubscriptionEventHandler implements RequestEventHandler {

    @Autowired
    private VendorRequestRepository requestRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    @Override
    public void handle(CustomMessage customMessage) {
        UsersStatusEntity usersStatusEntity = userStatusRepository.findByMsisdnAndVendorPlanId(customMessage.getMsisdn(), Long.valueOf(customMessage.getVendorPlanId()));
        if(usersStatusEntity == null) {
            //create user status entity TODO
        }
        //TODO do the action
        VendorRequestsEntity entity = createResponse("", "");
        requestRepository.save(entity);
    }

    private VendorRequestsEntity createResponse(String resultStatus, String correlationId) {
        VendorRequestsEntity entity = new VendorRequestsEntity();
        entity.setCdatetime(new Date());
        entity.setCorrelationid(correlationId);
        entity.setFetched(false);
        entity.setResultStatus("101");
        return entity;
    }
}
