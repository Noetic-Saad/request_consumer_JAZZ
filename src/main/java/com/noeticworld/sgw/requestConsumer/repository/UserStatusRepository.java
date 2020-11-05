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

    @Query(value = "SELECT * FROM public.users_status WHERE id=:id and free_trial_expiry<:fromDate",nativeQuery = true)
    UsersStatusEntity returnUserExpiredOrnOt(@Param("id") Long id, @Param("free_trial_expiry") Timestamp fromDate);

}
