package com.noeticworld.sgw.requestConsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeticworld.sgw.requestConsumer.service.RequestEvent;
import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RequestConsumer {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = {"hello"})
    public void receive(String msg) {
        try {
            CustomMessage customMessage = objectMapper.readValue(msg, CustomMessage.class);
            RequestEvent event = new RequestEvent(this, customMessage);
            applicationEventPublisher.publishEvent(event);
            System.out.println("processed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
