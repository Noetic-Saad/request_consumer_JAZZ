package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.RequestEventsEntity;
import com.noeticworld.sgw.requestConsumer.entities.ResponseTypeEntity;
import com.noeticworld.sgw.requestConsumer.entities.SubscriptionSettingEntity;
import com.noeticworld.sgw.requestConsumer.repository.RequestEventsRepository;
import com.noeticworld.sgw.requestConsumer.repository.ResponseTypeRepository;
import com.noeticworld.sgw.requestConsumer.repository.SubscriptionSettingRepository;
import org.elasticsearch.common.inject.Singleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Singleton
public class ConfigurationDataManagerService {

    private Map<Long, SubscriptionSettingEntity> subscriptionSettingEntityMap = new HashMap<>();
    private Map<String, ResponseTypeEntity> responseTypeEntityMap = new HashMap<>();
    private Map<String, RequestEventsEntity> requestEventsEntityMap = new HashMap<>();

    @Autowired private ResponseTypeRepository responseTypeRepository;
    @Autowired private SubscriptionSettingRepository subscriptionSettingRepository;
    @Autowired private RequestEventsRepository requestEventsRepository;

    public SubscriptionSettingEntity getSubscriptionEntity(long vendorPlanId) {
        return subscriptionSettingEntityMap.get(vendorPlanId);
    }

    public RequestEventsEntity getRequestEventsEntity(String code) {
        return requestEventsEntityMap.get(code);
    }

    public void updateSubscriptionEntity(SubscriptionSettingEntity subscriptionSettingEntity) {
        //save in db first
        subscriptionSettingEntityMap.put(subscriptionSettingEntity.getVendorPlanId(), subscriptionSettingEntity);
    }

    public void bootstapAndCacheConfigurationData() {
        //fetch data from db
        loadResponseTypes();
        loadSubscriptionSettings();
        loadRequestEvents();
    }

    private void loadRequestEvents() {
        Map<String, RequestEventsEntity> map = new HashMap<>();
        List<RequestEventsEntity> list = requestEventsRepository.findAll();
        list.forEach(entity -> map.put(entity.getCode(), entity));
        requestEventsEntityMap = map;
    }

    private void loadResponseTypes() {
        Map<String, ResponseTypeEntity> map = new HashMap<>();
        List<ResponseTypeEntity> list = responseTypeRepository.findAll();
        list.forEach(entity -> map.put(entity.getCode(), entity));
        responseTypeEntityMap = map;
    }

    private void loadSubscriptionSettings() {
        Map<Long, SubscriptionSettingEntity> map = new HashMap<>();
        List<SubscriptionSettingEntity> list = subscriptionSettingRepository.findAll();
        list.forEach(entity -> map.put(entity.getVendorPlanId(), entity));
        subscriptionSettingEntityMap = map;
    }

}
