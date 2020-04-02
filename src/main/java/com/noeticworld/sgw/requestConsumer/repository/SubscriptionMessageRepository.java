package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.SubscriptionMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionMessageRepository  extends JpaRepository<SubscriptionMessageEntity,Long> {
}
