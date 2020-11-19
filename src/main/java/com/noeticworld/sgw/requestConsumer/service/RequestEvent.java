package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.util.CustomMessage;
import org.springframework.context.ApplicationEvent;

public class RequestEvent extends ApplicationEvent {

    private CustomMessage message;

    public RequestEvent(Object source, CustomMessage message) {
        super(source);
        this.message = message;
    }

    public CustomMessage getMessage() {
        return message;
    }

    public void setMessage(CustomMessage message) {
        this.message = message;
    }
}
