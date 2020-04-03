package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorReportRepository extends JpaRepository<VendorReportEntity,Integer> {

    VendorReportEntity findByMsisdnAndVenodorPlanId(Long msisdn,Integer VendorPlanId);
}
