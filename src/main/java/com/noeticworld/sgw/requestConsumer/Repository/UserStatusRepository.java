package com.noeticworld.sgw.requestConsumer.Repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface UserStatusRepository extends JpaRepository<UsersStatusEntity,Integer> {

    Integer findStautsIdByMsisdnAndVendorPlanId(String msisds, Integer vendorPlanId);
    UsersStatusEntity findByMsisdnAndVendorPlanId(String msisds, Long vendorPlanId);
}
