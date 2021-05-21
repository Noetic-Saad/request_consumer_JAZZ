package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionMessageEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionMessageRepository;
import com.noeticworld.sgw.requestConsumer.repository.UserStatusRepository;
import com.noeticworld.sgw.requestConsumer.repository.UsersRepository;
import com.noeticworld.sgw.util.MtClient;
import com.noeticworld.sgw.util.MtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MtService {

    @Autowired
    MtClient mtClient;
    Logger log = LoggerFactory.getLogger(MtService.class.getName());
    @Autowired
    private ConfigurationDataManagerService dataService;
    @Autowired
    private SubscriptionMessageRepository subscriptionMessageRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    private String msg = "";

    public void sendSubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            UsersEntity userstatus = usersRepository.returnUserStatusId(msisdn);
            if (userstatus != null) {
                log.info("userstatusid " + userstatus);
                UsersStatusEntity us = userStatusRepository.returnUserExpiredOrnOt(userstatus.getId(), fromDate);
                if (us != null) {

                    msg = dataService.getMtMessage("jazz_sub_freetrial").getMsgText();
                    log.info("User Still in free Trial " + msg);
                } else {

                    msg = dataService.getMtMessage("jazz_sub").getMsgText();
                    log.info("Free Trial Expired " + msg);
                }

            } else {

                msg = dataService.getMtMessage("jazz_sub").getMsgText();
                log.info("Free Trial Expired " + msg);
            }

        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {
            msg = dataService.getMtMessage("zong_sub").getMsgText();
        } else {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            UsersEntity userstatus = usersRepository.returnUserStatusId(msisdn);
            if (userstatus != null) {
                log.info("userstatusid " + userstatus.getUserStatusId());
                UsersStatusEntity us = userStatusRepository.returnUserExpiredOrnOt(userstatus.getId(), fromDate);
                if (us != null) {
                    msg = dataService.getMtMessage("jazz_sub_freetrial").getMsgText();
                } else {

                    msg = dataService.getMtMessage("jazz_sub").getMsgText();
                    log.info("Free Trial Expired " + msg);
                }

            } else {
                msg = dataService.getMtMessage("jazz_sub").getMsgText();
            }
        }
        processMtRequest(msisdn, msg);

    }

    public void sendUnsubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            UsersEntity userstatus = usersRepository.returnUserStatusId(msisdn);
            if (userstatus != null) {
                log.info("userstatusid " + userstatus.getUserStatusId());
                UsersStatusEntity us = userStatusRepository.returnUserExpiredOrnOt(userstatus.getId(), fromDate);
                if (us != null) {
                    msg = dataService.getMtMessage("jazz_unsub_freetrial").getMsgText();
                    log.info("*************User Still in free Trial ************ Sending Message" + msg);
                } else {
                    msg = dataService.getMtMessage("jazz_unsub").getMsgText();
                    log.info("Free Trial Expired " + msg);
                }

            } else {

                msg = dataService.getMtMessage("jazz_unsub").getMsgText();
                log.info("*********Free Trial Expired***********" + msg);
            }
        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {

            msg = dataService.getMtMessage("zong_unsub").getMsgText();
        } else {
            Timestamp fromDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            UsersEntity userstatus = usersRepository.returnUserStatusId(msisdn);
            if (userstatus != null) {
                log.info("userstatusid " + userstatus.getUserStatusId());
                UsersStatusEntity us = userStatusRepository.returnUserExpiredOrnOt(userstatus.getId(), fromDate);
                if (us != null) {
                    msg = dataService.getMtMessage("jazz_unsub_freetrial").getMsgText();
                    log.info("*************User Still in free Trial ************ Sending Message : " + msg);
                } else {

                    msg = dataService.getMtMessage("jazz_unsub").getMsgText();
                    log.info("Free Trial Expired " + msg);
                }

            } else {
                msg = dataService.getMtMessage("jazz_unsub").getMsgText();
                log.info("*********Free Trial Expired jazz_unsub*********** : " + msg);
            }
        }
        processMtRequest(msisdn, msg);

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

        saveMessageRecord(msisdn, msg);
    }

    public void saveMessageRecord(Long msisnd, String msg) {
        SubscriptionMessageEntity subscriptionMessageEntity = new SubscriptionMessageEntity();
        subscriptionMessageEntity.setCdate(Timestamp.valueOf(LocalDateTime.now()));
        subscriptionMessageEntity.setMessageType("mt");
        subscriptionMessageEntity.setSmsStatus(1);
        subscriptionMessageEntity.setMessage(msg);
        subscriptionMessageEntity.setMsisdn(msisnd);
        subscriptionMessageRepository.save(subscriptionMessageEntity);

    }
}

