package com.noeticworld.sgw.requestConsumer.repository;

import com.noeticworld.sgw.requestConsumer.entities.ResponseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseTypeRepository extends JpaRepository<ResponseTypeEntity, Integer> {
}
