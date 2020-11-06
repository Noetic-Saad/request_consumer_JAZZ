package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UsersStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
@Transactional
@Repository
public
interface UserStatusRepository extends JpaRepository<UsersStatusEntity,Integer> {

    UsersStatusEntity findTopByUserIdAndVendorPlanIdAndStatusIdOrderByIdDesc(long userId, long vendorPlanId, int userStatusId);
    UsersStatusEntity findTopByUserIdAndVendorPlanIdOrderByIdDesc(long userId, long vendorPlanId);
    UsersStatusEntity findTopById(long id);

    @Query(value = "SELECT * FROM public.users_status WHERE id=:id and free_trial_expiry>:fromDate",nativeQuery = true)
    UsersStatusEntity returnUserExpiredOrnOt(@Param("id") Long id, @Param("fromDate") Timestamp fromDate);

    @Modifying
    @Query(value ="update public.users_status set free_trial_expiry = ?1, status = ?2 where id = ?3",nativeQuery = true)
    void setUserInfoById(Timestamp freetrial, Integer status,Long Id);

}
