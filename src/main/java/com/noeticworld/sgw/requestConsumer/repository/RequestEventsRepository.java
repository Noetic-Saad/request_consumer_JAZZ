package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.RequestEventsEntity;
import com.noeticworld.sgw.requestConsumer.entities.VendorPlanAccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface RequestEventsRepository extends JpaRepository<RequestEventsEntity, Integer> {

}
