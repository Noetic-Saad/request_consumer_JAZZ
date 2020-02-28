package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.util.CustomMessage;
import com.noeticworld.sgw.util.RequestProperties;

public interface RequestEventHandler {
    void handle(RequestProperties requestProperties) throws InterruptedException;
}
