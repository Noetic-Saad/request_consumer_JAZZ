package com.noeticworld.sgw.requestConsumer.service;

import com.noeticworld.sgw.requestConsumer.entities.*;
import com.noeticworld.sgw.requestConsumer.repository.*;
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
    private Map<String, EventTypesEntity> requestEventsEntityMap = new HashMap<>();
    private Map<Integer, UserStatusTypeEntity> userStatuseTypeMap = new HashMap<>();
    private Map<String, Integer> userStatusTypeIdsMap = new HashMap<>();
    private Map<Long, TestMsisdnsEntity> testMsisdnsMap = new HashMap<>();

    @Autowired private ResponseTypeRepository responseTypeRepository;
    @Autowired private SubscriptionSettingRepository subscriptionSettingRepository;
    @Autowired private RequestEventsRepository requestEventsRepository;
    @Autowired private UserStatusesLookupRepository userStatusTypeRepository;
    @Autowired private TestMsisdnsRepository testMsisdnsRepository;

    public SubscriptionSettingEntity getSubscriptionEntity(long vendorPlanId) {
        return subscriptionSettingEntityMap.get(vendorPlanId);
    }

    public EventTypesEntity getRequestEventsEntity(String code) {
        return requestEventsEntityMap.get(code);
    }

    public UserStatusTypeEntity getUserStatusType(int statusTypeId) {
        return userStatuseTypeMap.get(statusTypeId);
    }

    public int getUserStatusTypeId(String statusTypeName) {
        return userStatusTypeIdsMap.get(statusTypeName);
    }

    public String getResultStatusDescription(String code) {
        return responseTypeEntityMap.get(code).getDescription();
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
        loadUserStatuseTypes();
        loadTestMsisdns();
    }

    private void loadUserStatuseTypes() {
        List<UserStatusTypeEntity> list = userStatusTypeRepository.findAll();

        Map<Integer, UserStatusTypeEntity> objMap = new HashMap<>();
        list.forEach(statusType -> objMap.put(statusType.getId(), statusType));
        userStatuseTypeMap = objMap;

        Map<String, Integer> idsMap = new HashMap<>();
        list.forEach(statusType -> idsMap.put(statusType.getName(), statusType.getId()));
        userStatusTypeIdsMap = idsMap;
    }

    private void loadRequestEvents() {
        Map<String, EventTypesEntity> map = new HashMap<>();
        List<EventTypesEntity> list = requestEventsRepository.findAll();
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

    private void loadTestMsisdns() {
        List<TestMsisdnsEntity> list = testMsisdnsRepository.findAll();
        Map<Long, TestMsisdnsEntity> map = new HashMap<>();
        list.forEach(entity -> map.put(entity.getMsisdn(), entity));
        testMsisdnsMap = map;
    }

    public boolean isTestMsisdn(long msisdn) {
        return testMsisdnsMap.get(msisdn) != null;
    }
}
