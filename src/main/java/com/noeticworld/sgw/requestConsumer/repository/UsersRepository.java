package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface UsersRepository extends JpaRepository<UsersEntity, Long> {

    UsersEntity findByMsisdn(long msisdn);

}
