package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public
interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    UsersEntity findByMsisdn(long msisdn);

    @Query(value = "SELECT * FROM public.users WHERE msisdn=:msisdn ",nativeQuery = true)
    UsersEntity returnUserStatusId(@Param("msisdn") Long msisdn);
    @Query(value = "SELECT * from public.users where msisdn=:msisdn order by id desc limit 1" ,nativeQuery = true)
    UsersEntity FindByTopMSISDN(@Param("msisdn") long msisdn);
}
