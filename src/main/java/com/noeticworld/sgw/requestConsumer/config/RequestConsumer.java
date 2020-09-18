package com.noeticworld.sgw.requestConsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeticworld.sgw.requestConsumer.service.RequestEvent;
import com.noeticworld.sgw.requestConsumer.service.externalEvents.SubscriptionEventHandler;
import com.noeticworld.sgw.util.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RequestConsumer {

    Logger log = LoggerFactory.getLogger(RequestConsumer.class.getName());

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = {"subscriptionQueue"})
    public void receive(String msg) {

        try {
            CustomMessage customMessage = objectMapper.readValue(msg, CustomMessage.class);
            RequestEvent event = new RequestEvent(this, customMessage);
            applicationEventPublisher.publishEvent(event);
            log.info("CONSUMER SERVICE | REQUESTCONSUMER CLASS | EVENT PUBLISHED FOR MSISDN | "+ event.getMessage().getMsisdn());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
