package com.noeticworld.sgw.requestConsumer.Repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorPlanAccountsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public
interface VendorPlanAccountsRepository extends JpaRepository<VendorPlanAccountsEntity,Integer> {

}
