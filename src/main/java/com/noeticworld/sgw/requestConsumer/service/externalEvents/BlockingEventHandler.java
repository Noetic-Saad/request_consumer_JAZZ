package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.util.RequestProperties;
import org.springframework.stereotype.Service;

@Service
public class BlockingEventHandler implements RequestEventHandler {

    @Override
    public void handle(RequestProperties requestProperties) {

    }
}
