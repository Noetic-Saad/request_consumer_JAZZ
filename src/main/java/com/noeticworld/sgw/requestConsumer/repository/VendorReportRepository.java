package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorReportRepository extends JpaRepository<VendorReportEntity,Integer> {

    List<VendorReportEntity> findByMsisdnAndVenodorPlanId(Long msisdn, Integer VendorPlanId);
}
