package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface UserStatusRepository extends JpaRepository<UsersStatusEntity,Integer> {

    UsersStatusEntity findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(long userId, long vendorPlanId, int userStatusId);
    UsersStatusEntity findTopByUserIdAndVendorPlanIdOrderByIdDesc(long userId, long vendorPlanId);

}
