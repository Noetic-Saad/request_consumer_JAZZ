package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsEntity;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RequestProcessorService {

    //RESULT STATUS: 101 - in progress, 102 - successful

    @Autowired
    VendorRequestRepository requestRepository;

    public void process(CustomMessage customMessage) {

        VendorRequestsEntity entity = new VendorRequestsEntity();
        entity.setCdatetime(new Date());
        entity.setCorrelationid(customMessage.getCorelationId());
        entity.setFetched(false);
        entity.setResultStatus("101");
        requestRepository.save(entity);
        System.out.println("<<<<<<<<<<<<<<<< Request Processed >>>>>>>>>>>>>>>");
    }
}
