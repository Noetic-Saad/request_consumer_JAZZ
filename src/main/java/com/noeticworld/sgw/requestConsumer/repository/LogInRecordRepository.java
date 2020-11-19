package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.LoginRecordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogInRecordRepository extends JpaRepository<LoginRecordsEntity,Integer> {

    LoginRecordsEntity findTopBySessionIdAndMsisdn(String sessionId,long msisdn);
}
