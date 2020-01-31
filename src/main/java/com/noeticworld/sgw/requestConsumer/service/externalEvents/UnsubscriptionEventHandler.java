package com.noeticworld.sgw.requestConsumer.service.externalEvents;

import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestRepository;
import com.noeticworld.sgw.requestConsumer.service.ConfigurationDataManagerService;
import com.noeticworld.sgw.requestConsumer.service.MtService;
import com.noeticworld.sgw.util.RequestProperties;
import com.noeticworld.sgw.util.ResponseTypeConstants;
import com.noeticworld.sgw.util.UserStatusTypeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class UnsubscriptionEventHandler implements RequestEventHandler {

    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private VendorRequestRepository requestRepository;
    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired MtService mtService;
    private static final String JAZZ_MSG = "Dear Customer, you are successfully unSubscribed to Gamenow, To Subscribe, go to http://bit.ly/2s7au8P";

    @Override
    public void handle(RequestProperties requestProperties) {

        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        VendorPlansEntity vendorPlans = dataService.getVendorPlans(_user.getVendorPlanId());
        if(_user==null){
            createResponse(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND,requestProperties.getCorrelationId());
        }else {
            String resultCode =  changeUserStatus(_user);
            createResponse(resultCode,requestProperties.getCorrelationId());
            if(vendorPlans.getMtResponse() == 1) {
                mtService.sendUnsubMt(requestProperties.getMsisdn(), dataService.getVendorPlans(requestProperties.getVendorPlanId()));
            }
        }
    }
    private String changeUserStatus(UsersEntity users){

        UsersStatusEntity entity = userStatusRepository.findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(users.getId(),users.getVendorPlanId(),1);
        if(entity != null && entity.getStatusId()==1){
            UsersStatusEntity entity1 = new UsersStatusEntity();
            entity1.setUserId(users.getId());
            entity1.setStatusId(dataService.getUserStatusTypeId(UserStatusTypeConstants.UNSUBSCRIBED));
            entity1.setVendorPlanId(users.getVendorPlanId());
            entity1.setCdate(new Timestamp(new Date().getTime()));
            userStatusRepository.save(entity1);
            return ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL;
        }else if(entity.getStatusId()!=1){
            return ResponseTypeConstants.ALREADY_UNSUBSCRIBED;
        }else if (entity ==null){
            return ResponseTypeConstants.SUBSCRIBER_NOT_FOUND;
        }else {
            return ResponseTypeConstants.OTHER_ERROR;
        }
    }

    private void createResponse(String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        entity.setCdatetime(new Timestamp(new Date().getTime()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        if(resultStatus.equals(ResponseTypeConstants.ALREADY_UNSUBSCRIBED)) {
            entity.setDescription("Already UnSubscribed");
        }else if(resultStatus.equals(ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL)){
            entity.setDescription("UnSubscribed Successful");
        }else if (resultStatus.equals(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)){
            entity.setDescription("Subscriber Not Found");
        }else {
            entity.setResultStatus(ResponseTypeConstants.OTHER_ERROR);
            entity.setDescription("Other Error");
        }
        long id = requestRepository.save(entity).getId();
        System.out.println("Lates Id"+id);
    }
}
