package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.RequestEventsEntity;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.CustomMessage;
import com.noeticworld.sgw.util.RequestEventCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class RequestHandlerManager {

    @Autowired
    private ConfigurationDataManagerService configurationDataManagerService;

    public void manage(CustomMessage customMessage) {
        RequestEventsEntity requestEventsEntity = configurationDataManagerService.getRequestEventsEntity(customMessage.getAction());

        RequestEventHandler handler = null;
        if(requestEventsEntity.getCode().equals(RequestEventCodeConstants.SUBSCRIPTION_REQUEST_USER_INITIATED) ||
                requestEventsEntity.getCode().equals(RequestEventCodeConstants.SUBSCRIPTION_REQUEST_TELCO_INITIATED) ||
                requestEventsEntity.getCode().equals(RequestEventCodeConstants.SUBSCRIPTION_REQUEST_VENDOR_INITIATED)) {
            handler = new SubscriptionEventHandler();
        } else if(requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.UNSUBSCRIPTION_REQUEST_TELCO_INITIATED) ||
                requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.UNSUBSCRIPTION_REQUEST_USER_INITIATED) ||
                requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.UNSUBSCRIPTION_REQUEST_VENDOR_INITIATED)) {
            handler = new UnsubscriptionEventHandler();
        } else if(requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.CHARGE_ONLY_VENDOR_INITIATED)) {
            handler = new ChargeOnlyEventHandler();
        } else if(requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.BLOCKING_REQUEST_TELCO_INITIATED) ||
                requestEventsEntity.getCode().equalsIgnoreCase(RequestEventCodeConstants.BLOCKING_REQUEST_VENDOR_INITIATED)) {
            handler = new BlockingEventHandler();
        }
        if(handler == null) {
            //store in db with no action available info
            return;
        }
        handler.handle(customMessage);

    }
}
