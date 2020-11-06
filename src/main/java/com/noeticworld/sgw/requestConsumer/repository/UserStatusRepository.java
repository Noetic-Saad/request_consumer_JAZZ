package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    UsersStatusEntity returnUserExpiredOrnOt(@Param("id") Long id, @Param("fromDate") Timestamp fromDate);

    @Modifying
    @Query("update public.users_status us set us.free_trial_expiry = ?1, us.status = ?2 where u.id = ?3")
    void setUserInfoById(Timestamp freetrial, Integer status,Long Id);

}
