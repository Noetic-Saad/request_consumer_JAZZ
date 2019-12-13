package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.VendorPlansEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public
interface VendorPlansEntityRepository extends JpaRepository<VendorPlansEntity,Integer> {

    @Query(value = "SELECT vp.validity_days FROM public.vendor_plans vp LEFT JOIN public.vendor_plan_accounts vpa ON vp.id = vpa.plan_id where vpa.id = :vpAccountId" ,nativeQuery = true)
    public Integer getValidityDays(Integer vpAccountId);
}
