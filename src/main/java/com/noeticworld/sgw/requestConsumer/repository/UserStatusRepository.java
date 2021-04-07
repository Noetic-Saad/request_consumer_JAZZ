package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public
interface UserStatusRepository extends JpaRepository<UsersStatusEntity,Integer> {

    UsersStatusEntity findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(long userId, long vendorPlanId, int userStatusId);

    UsersStatusEntity findTopByUserIdAndVendorPlanIdOrderByIdDesc(long userId, long vendorPlanId);

    UsersStatusEntity findTopById(long id);

    @Query(value = "SELECT * FROM public.users_status WHERE user_id=:id and free_trial>:fromDate",nativeQuery = true)
    UsersStatusEntity returnUserExpiredOrnOt(@Param("id") Long id, @Param("fromDate") Timestamp fromDate);

    @Query(value = "SELECT * from public.users_status where free_trial>:today and user_id=:user_id" ,nativeQuery = true)
    List<UsersStatusEntity> IsFreeTrialUser(@Param("today") Timestamp today,@Param("user_id") long user_id);

    @Query(value = "SELECT * from public.users_status where user_id=:u_id order by id desc limit 1" ,nativeQuery = true)
    UsersStatusEntity UnsubStatus(@Param("u_id") long id);


}
