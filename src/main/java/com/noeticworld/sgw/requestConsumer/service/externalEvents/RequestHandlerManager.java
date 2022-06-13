package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.EventTypesEntity;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.util.RequestActionCodeConstants;
import com.noeticworld.sgw.util.RequestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class RequestHandlerManager {

    @Autowired
    private ConfigurationDataManagerService configurationDataManagerService;
    @Autowired
    private SubscriptionEventHandler subscriptionEventHandler;
    @Autowired
    private UnsubscriptionEventHandler unsubscriptionEventHandler;
    @Autowired
    private ChargeOnlyEventHandler chargeOnlyEventHandler;
    @Autowired
    private BlockingEventHandler blockingEventHandler;
    @Autowired
    private OtpVerificationHandler otpVerificationHandler;
    @Autowired
    private LogInEventHandler logInEventHandler;
    @Autowired
    private LogOutEventHandler logOutEventHandler;
    @Autowired
    private AutLogInHandler autLogInHandler;


    public void manage(RequestProperties requestProperties) throws URISyntaxException {
        EventTypesEntity eventTypesEntity = configurationDataManagerService.getRequestEventsEntity(requestProperties.getRequestAction());

        if (eventTypesEntity.getCode().equals(RequestActionCodeConstants.SUBSCRIPTION_REQUEST_USER_INITIATED) ||
                eventTypesEntity.getCode().equals(RequestActionCodeConstants.SUBSCRIPTION_REQUEST_TELCO_INITIATED) ||
                eventTypesEntity.getCode().equals(RequestActionCodeConstants.SUBSCRIPTION_REQUEST_VENDOR_INITIATED)) {
            subscriptionEventHandler.handle(requestProperties);

        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.UNSUBSCRIPTION_REQUEST_TELCO_INITIATED) ||
                eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.UNSUBSCRIPTION_REQUEST_USER_INITIATED) ||
                eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.UNSUBSCRIPTION_REQUEST_VENDOR_INITIATED)) {
            unsubscriptionEventHandler.handle(requestProperties);

        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.CHARGE_ONLY_VENDOR_INITIATED)) {

            chargeOnlyEventHandler.handle(requestProperties);

        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.BLOCKING_REQUEST_TELCO_INITIATED) ||
                eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.BLOCKING_REQUEST_VENDOR_INITIATED)) {

            blockingEventHandler.handle(requestProperties);

        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.LOGIN_REQUEST_USER)) {
            logInEventHandler.handle(requestProperties);
        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.LOGOUT_REQUEST_USER)) {
            logOutEventHandler.handle(requestProperties);
        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.OTP_VERIFICATION)) {
            otpVerificationHandler.handle(requestProperties);
        } else if (eventTypesEntity.getCode().equalsIgnoreCase(RequestActionCodeConstants.AUTO_LOGIN_REQUEST_USER)) {
            autLogInHandler.handle(requestProperties);
        }
    }
}
