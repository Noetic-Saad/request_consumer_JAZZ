package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.OtpRecordsEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisRepository {

    private HashOperations hashOperations;

    private RedisTemplate redisTemplate;

    private String VendorRequestEntityKEY = "VENDOR_ACESS_KEY";
    private String OTPEntityKEY = "OTP_KEY";

    public RedisRepository(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public void saveVendorRequest(VendorRequestsStateEntity entity){
        hashOperations.put(VendorRequestEntityKEY, entity.getCorrelationid(), entity);
    }

    public void saveOtpRecord(OtpRecordsEntity entity){
        hashOperations.put(OTPEntityKEY, entity.getMsisdn(), entity);
    }

    public List findAllOTPOfMsisdn(Long Msisdn){
        return (List<OtpRecordsEntity>)hashOperations.get(OTPEntityKEY, Msisdn);
    }

    public VendorRequestsStateEntity findVendorRequestStatus(String CorelationId){
        return (VendorRequestsStateEntity) hashOperations.get(VendorRequestEntityKEY, CorelationId);
    }

//    public void update(VendorAccountAccessEntity entity){
//        save(entity);
//    }

    public void deleteByAccessToken(String token){
        hashOperations.delete("ACCES_TOKEN", token);
    }




}
