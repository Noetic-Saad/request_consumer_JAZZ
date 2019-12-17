package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.UserStatusTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusesLookupRepository extends JpaRepository<UserStatusTypeEntity,Integer> {
}
