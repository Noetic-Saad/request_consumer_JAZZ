package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.GamesBillingRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface GamesBillingRecordRepository  extends JpaRepository<GamesBillingRecordEntity,Long> {

    @Query(value = "SELECT * FROM public.games_billing_record WHERE msisdn=:msisdn and is_charged = 1 and cdate BETWEEN :fromDate and :toDate",nativeQuery = true)
    List<GamesBillingRecordEntity> isAlreadyChargedForToday(@Param("msisdn") Long msisdn, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

    @Query(value = "SELECT * FROM public.games_billing_record WHERE msisdn=:msisdn and is_charged = 1 and cdate BETWEEN :fromDate and :toDate",nativeQuery = true)
    List<GamesBillingRecordEntity> isAlreadyChargedFor7Days(@Param("msisdn") Long msisdn, @Param("fromDate") Timestamp fromDate, @Param("toDate") Timestamp toDate);

}
