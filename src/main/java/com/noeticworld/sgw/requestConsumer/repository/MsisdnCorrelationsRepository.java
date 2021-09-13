package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.MsisdnCorrelations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsisdnCorrelationsRepository extends JpaRepository<MsisdnCorrelations, Long> {
}
