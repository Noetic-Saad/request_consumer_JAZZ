package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.VendorPlansEntityRepository;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.util.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public
class UserStatusService {

    Logger logger = LoggerFactory.getLogger(UserStatusService.class);
    @Autowired
    UserStatusRepository userStatusRepository;
    @Autowired
    VendorPlansEntityRepository vendorPlansEntityRepository;


    public
    void subscribe(CustomMessage msg){

        logger.info("Testing Logs Here");
       Integer days =vendorPlansEntityRepository.getValidityDays(Integer.parseInt(msg.getVendorPlanId()));
       UsersStatusEntity usersStatusEntity = new UsersStatusEntity();
        usersStatusEntity.setMsisdn(msg.getMsisdn());
        usersStatusEntity.setVendorPlanId(Long.parseLong(msg.getVendorPlanId()));
        long status_id;
        if(msg.getAction().equalsIgnoreCase("subscribe")){
            status_id = 1;
        }else if(msg.getAction().equalsIgnoreCase("unsub")){
            status_id = 2;
        }else if(msg.getAction().equalsIgnoreCase("purge")){
            status_id = 3;
        }else if(msg.getAction().equalsIgnoreCase("blacklist")){
            status_id = 4;
        }else {
            status_id = 5;
        }
        usersStatusEntity.setStatus(1);
        usersStatusEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        usersStatusEntity.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        usersStatusEntity.setStatusId(status_id);
        usersStatusEntity.setExpiryDatetime(Timestamp.valueOf(LocalDateTime.now().plusDays(days)));
        try {
            userStatusRepository.save(usersStatusEntity);
            logger.info("Subscribed Successfully");
        }catch (Exception e){
            logger.error("Exception Caught Here "+e.getCause());
        }
    }
    public
    void unsubscribe(CustomMessage msg){

        logger.info("Testing Logs Here");
        Integer days =vendorPlansEntityRepository.getValidityDays(Integer.parseInt(msg.getVendorPlanId()));
        UsersStatusEntity usersStatusEntity = new UsersStatusEntity();
        long status_id;
        if(msg.getAction().equalsIgnoreCase("subscribe")){
            status_id = 1;
        }else if(msg.getAction().equalsIgnoreCase("unsub")){
            status_id = 2;
        }else if(msg.getAction().equalsIgnoreCase("purge")){
            status_id = 3;
        }else if(msg.getAction().equalsIgnoreCase("blacklist")){
            status_id = 4;
        }else {
            status_id = 5;
        }
        usersStatusEntity.setStatus(0);
        usersStatusEntity.setModifyDate(Timestamp.valueOf(LocalDateTime.now()));
        usersStatusEntity.setStatusId(status_id);
        try {
            userStatusRepository.save(usersStatusEntity);
        }catch (Exception e){
            System.out.println("Exception ->"+e.getCause());
        }
    }

    public
    void getStatus(CustomMessage customMessage) {
        //userStatusRepository.findByMsisdnAndVendorPlanId(customMessage.getMsisdn(),Integer.parseInt(customMessage.getVendorPlanId()));
        UsersStatusEntity usersStatusEntity = userStatusRepository.findByMsisdnAndVendorPlanId(customMessage.getMsisdn(),Long.parseLong(customMessage.getVendorPlanId()));
        logger.info("Success"+usersStatusEntity.getStatusId());
    }
}
