package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.util.RequestProperties;

import java.net.URISyntaxException;

public interface RequestEventHandler {
    void handle(RequestProperties requestProperties) throws URISyntaxException;
}
