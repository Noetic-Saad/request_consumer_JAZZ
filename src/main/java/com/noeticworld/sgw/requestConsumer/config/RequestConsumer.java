package com.noeticworld.sgw.requestConsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeticworld.sgw.requestConsumer.Service.UserStatusService;
import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Component
public class RequestConsumer {

    @Autowired
    UserStatusService userStatusService;

    @RabbitListener(queues = {"hello"})
    public void receive(Message msg) throws IOException {
        Map<String,String> map;
        map = new ObjectMapper().readValue(new String(msg.getBody()),Map.class);
        CustomMessage customMessage = new CustomMessage(map.get("text"),map.get("msisdn"),map.get("action"),map.get("corelationId"),map.get("vendorPlanId"));
        System.out.println("Map");
        if(customMessage.getAction().equalsIgnoreCase("unsub")){
            userStatusService.unsubscribe(customMessage);
        }else if(customMessage.getAction().equalsIgnoreCase("unsub")){
            userStatusService.subscribe(customMessage);
        }else {
            userStatusService.getStatus(customMessage);
        }
    }

}
