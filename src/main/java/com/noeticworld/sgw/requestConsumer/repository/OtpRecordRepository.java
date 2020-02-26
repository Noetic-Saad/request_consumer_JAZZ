package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.OtpRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRecordRepository extends JpaRepository<OtpRecordsEntity,Integer> {

    OtpRecordsEntity findTopByMsisdnAndVendorPlanIdAndOtpNumber(long msisd,long vendorPlanId,Integer OtpNumber);
}
