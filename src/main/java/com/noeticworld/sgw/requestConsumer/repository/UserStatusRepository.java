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
    @Query(value = "SELECT * FROM public.users_status WHERE user_id=:id and free_trial>:fromDate",nativeQuery = true)
    UsersStatusEntity returnUserExpiredOrnOt(@Param("id") Long id, @Param("fromDate") Timestamp fromDate);
    @Query(value = "SELECT * from public.users_status where expiryDate<:cdate" ,nativeQuery = true)
    UsersStatusEntity GetFreeTrial(@Param("cdate") Timestamp cdate);
    @Query(value = "SELECT status_id from public.users_status where id:id order by id desc" ,nativeQuery = true)
    int UnsubStatus(@Param("id") long id);

}
