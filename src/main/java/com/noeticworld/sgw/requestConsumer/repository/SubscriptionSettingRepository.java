package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.ResponseTypeEntity;
import com.noeticworld.sgw.requestConsumer.entities.SubscriptionSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionSettingRepository extends JpaRepository<SubscriptionSettingEntity, Long> {
}
