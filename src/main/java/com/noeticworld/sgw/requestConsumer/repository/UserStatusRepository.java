package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public
interface UserStatusRepository extends JpaRepository<UsersStatusEntity,Integer> {

    UsersStatusEntity findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(long userId, long vendorPlanId, int userStatusId);
    UsersStatusEntity findTopByUserIdAndVendorPlanIdOrderByIdDesc(long userId, long vendorPlanId);
    UsersStatusEntity findTopById(long id);

    @Query(value = "SELECT * from user_status where expiryDate<:cdate" ,nativeQuery = true)
    UsersStatusEntity GetFreeTrial(@Param("cdate") Timestamp cdate);

}
