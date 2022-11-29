package com.noeticworld.sgw.requestConsumer.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noeticworld.sgw.requestConsumer.entities.OtpRecordsEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RedisRepository {

    private HashOperations hashOperations;

    private RedisTemplate redisTemplate;

    private String VendorRequestEntityKEY = "VENDOR_ACESS_KEY";
    private String OTPEntityKEY = "OTP_KEY";

    Logger log = LoggerFactory.getLogger(RedisRepository.class);

    public RedisRepository(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public void saveVendorRequest(String key, String entity){
        hashOperations.put(VendorRequestEntityKEY, key, entity);
        log.info("REDISREPOSITORY || SAVEVENDORREQUEST || ", entity.toString());
    }

    public void saveOtpRecord(String key,String entity){
        hashOperations.put(OTPEntityKEY, key, entity);
        log.info("AFTER || REDISREPOSITORY || SAVEOTPRECORD || " + hashOperations.values(OTPEntityKEY));
    }

    public List<OtpRecordsEntity> findAllOTPOfMsisdn(String Msisdn){
        log.info("REDISREPOSITORY || FINDALLOTPOFMSISDN || " + Msisdn);
        List<OtpRecordsEntity> otprecordlist = new ArrayList<>();
        List listofids =  hashOperations.values(OTPEntityKEY);
        for (int i = 0; i <listofids.size() ; i++) {
            String  record = (String) listofids.get(i);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                OtpRecordsEntity requestStatus = objectMapper.readValue(record, OtpRecordsEntity.class);
                otprecordlist.add(requestStatus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("REDISREPOSITORY || FINDALLOTPOFMSISDN || " + otprecordlist.toString());
        return otprecordlist;
    }

    public VendorRequestsStateEntity findVendorRequestStatus(String CorelationId){
        log.info("LINE 64 || " + CorelationId);
        String vendor = (String) hashOperations.get(VendorRequestEntityKEY, CorelationId);
        log.info(""+hashOperations.get(VendorRequestEntityKEY, CorelationId));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VendorRequestsStateEntity vendorRequestStatusEntity = objectMapper.readValue(vendor, VendorRequestsStateEntity.class);
            log.info("LINE 64 || " + vendorRequestStatusEntity.toString());
            log.info("REDISREPOSITORY || FINDOTPRECORD || " + vendorRequestStatusEntity.toString());
            return vendorRequestStatusEntity;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
