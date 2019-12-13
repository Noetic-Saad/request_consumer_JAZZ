package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.util.CustomMessage;

public interface RequestEventHandler {
    void handle(CustomMessage customMessage);
}
