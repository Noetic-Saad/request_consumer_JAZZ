package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.config.RequestConsumer;
import com.noeticworld.sgw.util.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class ApplicationEventProcessorService implements ApplicationListener<RequestEvent> {

    Logger log = LoggerFactory.getLogger(ApplicationEventProcessorService.class.getName());

    @Autowired
    private RequestProcessorService requestProcessorService;

    @Override
    public void onApplicationEvent(RequestEvent requestEvent) {
        log.info("CONSUMER SERVICE | APPLICATIONEVENTPROCESSOR SERVICE CLASS | EVENT PUBLISHED FOR MSISDN | "+ requestEvent .getMessage().getMsisdn());
        CustomMessage customMessage = requestEvent.getMessage();
        requestProcessorService.process(customMessage);
    }
}
