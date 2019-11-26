package com.noeticworld.sgw.requestConsumer.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class RequestConsumer {

    //https://medium.com/@marcosstefani/rabbitmq-with-spring-boot-d05197fce05e
    @RabbitListener(queues = {"hello"})
    public void receive(Message msg) {
//    public void receive(String msg) {
        System.out.println("Received msg --> " + msg.toString());
    }
}
