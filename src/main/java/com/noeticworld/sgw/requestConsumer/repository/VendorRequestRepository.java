package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.model.VendorRequestsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRequestRepository extends JpaRepository<VendorRequestsEntity, Long> {
}
