package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorRequestsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRequestRepository extends JpaRepository<VendorRequestsEntity, Long> {
}
