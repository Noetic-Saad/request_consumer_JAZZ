package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionSettingRepository extends JpaRepository<SubscriptionSettingEntity, Long> {

    List<SubscriptionSettingEntity> findByRenewalSetting(int renewalSetting);
}
