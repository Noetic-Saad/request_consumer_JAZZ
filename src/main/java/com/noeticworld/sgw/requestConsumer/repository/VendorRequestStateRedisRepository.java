package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRequestStateRedisRepository {

    void saveVendorRequest(VendorRequestsStateEntity vendorRequestsStateEntity);
    VendorRequestsStateEntity findByCorrelationId(String correlationId);
}
