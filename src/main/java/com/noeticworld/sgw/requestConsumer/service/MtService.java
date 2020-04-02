package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionMessageEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionMessageRepository;
import com.noeticworld.sgw.util.MtClient;
import com.noeticworld.sgw.util.MtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class MtService {

    @Autowired
    MtClient mtClient;
    @Autowired
    private ConfigurationDataManagerService dataService;
    @Autowired
    private SubscriptionMessageRepository subscriptionMessageRepository;

    private String msg = "";

    public void sendSubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            msg = dataService.getMtMessage("jazz_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_sub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {
            msg = dataService.getMtMessage("zong_sub").getMsgText();
        } else {
            msg = dataService.getMtMessage("warid_sub").getMsgText();
        }
        processMtRequest(msisdn,msg);

    }

    public void sendUnsubMt(long msisdn, VendorPlansEntity vendorPlansEntity) {

        if (vendorPlansEntity.getOperatorId() == dataService.getJazz()) {
            msg = dataService.getMtMessage("jazz_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getTelenor()) {
            msg = dataService.getMtMessage("telenor_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getUfone()) {
            msg = dataService.getMtMessage("ufone_unsub").getMsgText();
        } else if (vendorPlansEntity.getOperatorId() == dataService.getZong()) {
            msg = dataService.getMtMessage("zong_unsub").getMsgText();
        } else {
            msg = dataService.getMtMessage("warid_unsub").getMsgText();
        }
        processMtRequest(msisdn,msg);

    }

    public void processMtRequest(long msisdn, String msg) {

        MtProperties mtProperties = new MtProperties();
        mtProperties.setUsername("tpay@noetic");
        mtProperties.setPassword("tpay@n03t1c2019");
        mtProperties.setServiceId("1044");
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

