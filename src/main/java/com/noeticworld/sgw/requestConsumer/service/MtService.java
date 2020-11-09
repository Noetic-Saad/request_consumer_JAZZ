package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionMessageEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionMessageRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.util.MtClient;
import com.noeticworld.sgw.util.MtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MtService {

    @Autowired
    MtClient mtClient;
    @Autowired
    private ConfigurationDataManagerService dataService;
    @Autowired
    private SubscriptionMessageRepository subscriptionMessageRepository;
    @Autowired private UsersRepository usersRepository;
    @Autowired private UserStatusRepository userStatusRepository;
    Logger log = LoggerFactory.getLogger(MtService.class.getName());
    private String msg = "";

    public void sendSubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            Long userstatusid=usersRepository.returnUserStatusId(msisdn);
            log.info("userstatusid "+userstatusid);
            UsersStatusEntity us=userStatusRepository.returnUserExpiredOrnOt(userstatusid,fromDate);
            if(us!=null){
                log.info("User Still in free Trial ");
                msg = dataService.getMtMessage("jazz_sub_freetrial").getMsgText();
            }
            else {
                log.info("Free Trial Expired ");
                msg = dataService.getMtMessage("jazz_sub").getMsgText();
            }

        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {
            msg = dataService.getMtMessage("zong_sub").getMsgText();
        } else {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            Long userstatusid=usersRepository.returnUserStatusId(msisdn);

            UsersStatusEntity us=userStatusRepository.returnUserExpiredOrnOt(userstatusid,fromDate);
            if(us!=null){
                msg = dataService.getMtMessage("jazz_sub_freetrial").getMsgText();
            }
            else {
                msg = dataService.getMtMessage("jazz_sub").getMsgText();
            }
        }
        processMtRequest(msisdn,msg);

    }

    public void sendUnsubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            Long userstatusid=usersRepository.returnUserStatusId(msisdn);
            log.info("userstatusid"+userstatusid);
            UsersStatusEntity us=userStatusRepository.returnUserExpiredOrnOt(userstatusid,fromDate);
            if(us!=null){
                log.info("*************User Still in free Trial ************");
                msg = dataService.getMtMessage("jazz_unsub_freetrial").getMsgText();
            }
            else {
                log.info("*********Free Trial Expired***********");
                msg = dataService.getMtMessage("jazz_unsub").getMsgText();
            }
        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {

            msg = dataService.getMtMessage("zong_unsub").getMsgText();
        } else {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            Long userstatusid=usersRepository.returnUserStatusId(msisdn);

            UsersStatusEntity us=userStatusRepository.returnUserExpiredOrnOt(userstatusid,fromDate);
            if(us!=null){
                msg = dataService.getMtMessage("jazz_unsub_freetrial").getMsgText();
            }
            else {
                msg = dataService.getMtMessage("jazz_unsub").getMsgText();
            }
        }
        processMtRequest(msisdn,msg);

    }

    public void processMtRequest(long msisdn, String msg) {

        MtProperties mtProperties = new MtProperties();
        mtProperties.setUsername("gamenow@noetic");
        mtProperties.setPassword("g@m3now");
        mtProperties.setServiceId("1061");
        mtProperties.setData(msg);
        mtProperties.setMsisdn(Long.toString(msisdn));
        mtProperties.setShortCode("3444");
        mtClient.sendMt(mtProperties);
        saveMessageRecord(msisdn,msg);
    }

    public void saveMessageRecord(Long msisnd,String msg){
        SubscriptionMessageEntity subscriptionMessageEntity = new SubscriptionMessageEntity();
        subscriptionMessageEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        subscriptionMessageEntity.setMessageType("mt");
        subscriptionMessageEntity.setSmsStatus(1);
        subscriptionMessageEntity.setMessage(msg);
        subscriptionMessageEntity.setMsisdn(msisnd);
        subscriptionMessageRepository.save(subscriptionMessageEntity);

    }
}

