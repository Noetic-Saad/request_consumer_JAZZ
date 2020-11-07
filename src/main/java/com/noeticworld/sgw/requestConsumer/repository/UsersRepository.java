package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.GamesBillingRecordEntity;
import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public
interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    UsersEntity findByMsisdn(long msisdn);
    @Query(value = "SELECT user_status_id FROM public.users WHERE msisdn=:msisdn ",nativeQuery = true)
    Long returnUserStatusId(@Param("msisdn") Long msisdn);

}
