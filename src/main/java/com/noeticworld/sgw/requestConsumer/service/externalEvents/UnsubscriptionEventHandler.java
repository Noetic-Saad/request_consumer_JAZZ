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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class UnsubscriptionEventHandler implements RequestEventHandler {

    Logger log = LoggerFactory.getLogger(UnsubscriptionEventHandler.class.getName());

    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    @Autowired private VendorRequestRepository requestRepository;
    @Autowired private ConfigurationDataManagerService dataService;
    @Autowired MtService mtService;

    @Override
    public void handle(RequestProperties requestProperties) {

        UsersEntity _user = usersRepository.findByMsisdn(requestProperties.getMsisdn());
        VendorPlansEntity vendorPlans = dataService.getVendorPlans(_user.getVendorPlanId());
        if(_user==null){
            log.info("CONSUMER SERVICE | UnsubscriptionEventHandler CLASS | MSISDN "+requestProperties.getMsisdn()+" NOT FOUND");
            createResponse(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND,requestProperties.getCorrelationId());
        }else {
            String resultCode =  changeUserStatus(_user,vendorPlans.getSubCycle());
            createResponse(resultCode,requestProperties.getCorrelationId());
            if(vendorPlans.getMtResponse() == 1) {
                mtService.sendUnsubMt(requestProperties.getMsisdn(), vendorPlans);
            }
        }
    }
    private String changeUserStatus(UsersEntity users,Integer subCycleId){

        UsersStatusEntity entity = userStatusRepository.findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(users.getId(),users.getVendorPlanId(),1);
        if(entity != null && entity.getStatusId()==dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)){
            UsersStatusEntity entity1 = new UsersStatusEntity();
            entity1.setUserId(users.getId());
            entity1.setStatusId(dataService.getUserStatusTypeId(UserStatusTypeConstants.UNSUBSCRIBED));
            entity1.setVendorPlanId(users.getVendorPlanId());
            entity1.setCdate(new Timestamp(new Date().getTime()));
            entity1.setExpiryDatetime(new Timestamp(new Date().getTime()));
            entity1.setSubCycleId(subCycleId);
            entity1.setAttempts(0);
            long userStatusId = userStatusRepository.save(entity1).getId();
            users.setUserStatusId((int) userStatusId);
            usersRepository.save(users);
            return ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL;
        }else if(entity.getStatusId()!=dataService.getUserStatusTypeId(UserStatusTypeConstants.SUBSCRIBED)){
            log.info("CONSUMER SERVICE | UnsubscriptionEventHandler CLASS | MSISDN "+users.getMsisdn()+" ALREADY UNSUBSCRIBED");
            return ResponseTypeConstants.ALREADY_UNSUBSCRIBED;
        }else if (entity ==null){
            log.info("CONSUMER SERVICE | UnsubscriptionEventHandler CLASS | MSISDN "+users.getMsisdn()+" NOT FOUND");
            return ResponseTypeConstants.SUBSCRIBER_NOT_FOUND;
        }else {
            return ResponseTypeConstants.OTHER_ERROR;
        }
    }

    private void createResponse(String resultStatus, String correlationId) {
        VendorRequestsStateEntity entity = requestRepository.findByCorrelationid(correlationId);
        boolean isNull = true;
        if(entity==null){
            while (isNull){
                entity  = requestRepository.findByCorrelationid(correlationId);
                if(entity!=null){
                    isNull = false;
                }
            }
        }
        entity.setCdatetime(Timestamp.valueOf(LocalDateTime.now()));
        entity.setFetched(false);
        entity.setResultStatus(resultStatus);
        if(resultStatus.equals(ResponseTypeConstants.ALREADY_UNSUBSCRIBED)) {
            entity.setDescription(ResponseTypeConstants.ALREAD_SUBSCRIBED_MSG);
        }else if(resultStatus.equals(ResponseTypeConstants.UNSUSBCRIBED_SUCCESSFULL)){
            entity.setDescription(ResponseTypeConstants.UNSUBSCRIBEDFULL_MSG);
        }else if (resultStatus.equals(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND)){
            entity.setDescription(ResponseTypeConstants.SUBSCRIBER_NOT_FOUND_MSG);
        }else {
            entity.setResultStatus(ResponseTypeConstants.OTHER_ERROR);
            entity.setDescription(ResponseTypeConstants.OTHER_ERROR_MSG);
        }
        requestRepository.save(entity);
    }
}
