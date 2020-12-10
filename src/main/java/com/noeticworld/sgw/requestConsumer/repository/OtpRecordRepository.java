package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.OtpRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRecordRepository extends JpaRepository<OtpRecordsEntity,Integer> {
    @Query(value = "SELECT * FROM public.otp_records WHERE msisdn=:msisdn order by id desc limit 1",nativeQuery = true)
    OtpRecordsEntity findtoprecord(@Param("msisdn") long msisdn);
    OtpRecordsEntity findTopByMsisdnAndOtpNumber(long msisdn,Integer OtpNumber);

}
