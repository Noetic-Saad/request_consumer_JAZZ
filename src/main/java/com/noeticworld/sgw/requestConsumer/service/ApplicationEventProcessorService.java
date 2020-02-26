package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationEventProcessorService implements ApplicationListener<RequestEvent> {

    @Autowired
    private RequestProcessorService requestProcessorService;

    @Override
    public void onApplicationEvent(RequestEvent requestEvent) {
        System.out.println("Received spring custom event - " + requestEvent.getMessage().toString());
        CustomMessage customMessage = requestEvent.getMessage();
        requestProcessorService.process(customMessage);
    }
}
