package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import com.noeticworld.sgw.requestConsumer.repository.VendorRequestStateRedisRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VendorRequestService implements VendorRequestStateRedisRepository {

    private HashOperations hashOperations;
    private RedisTemplate redisTemplate;

    public VendorRequestService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    @CachePut(value = "vendor_request_state",key = "#p0")
    @Override
    public void saveVendorRequest(VendorRequestsStateEntity vendorRequestsStateEntity) {
        hashOperations.put("vendor_request_state",vendorRequestsStateEntity.getCorrelationid(),vendorRequestsStateEntity);
    }

    @Cacheable(value = "vendor_request_state",key = "#p0")
    @Override
    public VendorRequestsStateEntity findByCorrelationId(String correlationId) {
        return (VendorRequestsStateEntity) hashOperations.get("vendor_request_state",correlationId);
    }


}
