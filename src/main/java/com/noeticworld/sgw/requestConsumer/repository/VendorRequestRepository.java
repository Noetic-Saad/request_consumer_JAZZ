package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRequestRepository extends JpaRepository<VendorRequestsStateEntity, Long> {

    VendorRequestsStateEntity findByCorrelationid(String correlationId);
}
