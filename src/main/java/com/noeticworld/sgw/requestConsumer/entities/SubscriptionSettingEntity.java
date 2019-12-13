package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "subscription_setting", schema = "public", catalog = "sgw")
public class SubscriptionSettingEntity {
    private long id;
    private long vendorPlanId;
    private Integer vendorId;
    private Time autoRenewalStartTime;
    private Integer failedRenewalRetryCount;
    private Time failedRenewalRetryDelayHours;
    private Time timeOver;
    private boolean active;
    private Integer subscriptionCycleDays;
    private Integer purgeCycleDays;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public long getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(long vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    @Basic
    @Column(name = "vendor_id")
    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    @Basic
    @Column(name = "auto_renewal_start_time")
    public Time getAutoRenewalStartTime() {
        return autoRenewalStartTime;
    }

    public void setAutoRenewalStartTime(Time autoRenewalStartTime) {
        this.autoRenewalStartTime = autoRenewalStartTime;
    }

    @Basic
    @Column(name = "failed_renewal_retry_count")
    public Integer getFailedRenewalRetryCount() {
        return failedRenewalRetryCount;
    }

    public void setFailedRenewalRetryCount(Integer failedRenewalRetryCount) {
        this.failedRenewalRetryCount = failedRenewalRetryCount;
    }

    @Basic
    @Column(name = "failed_renewal_retry_delay_hours")
    public Time getFailedRenewalRetryDelayHours() {
        return failedRenewalRetryDelayHours;
    }

    public void setFailedRenewalRetryDelayHours(Time failedRenewalRetryDelayHours) {
        this.failedRenewalRetryDelayHours = failedRenewalRetryDelayHours;
    }

    @Basic
    @Column(name = "time_over")
    public Time getTimeOver() {
        return timeOver;
    }

    public void setTimeOver(Time timeOver) {
        this.timeOver = timeOver;
    }

    @Basic
    @Column(name = "is_active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    @Basic
    @Column(name = "subscription_cycle_days")
    public Integer getSubscriptionCycleDays() {
        return subscriptionCycleDays;
    }

    public void setSubscriptionCycleDays(Integer subscriptionCycleDays) {
        this.subscriptionCycleDays = subscriptionCycleDays;
    }

    @Basic
    @Column(name = "purge_cycle_days")
    public Integer getPurgeCycleDays() {
        return purgeCycleDays;
    }

    public void setPurgeCycleDays(Integer purgeCycleDays) {
        this.purgeCycleDays = purgeCycleDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionSettingEntity that = (SubscriptionSettingEntity) o;
        return id == that.id &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(vendorId, that.vendorId) &&
                Objects.equals(autoRenewalStartTime, that.autoRenewalStartTime) &&
                Objects.equals(failedRenewalRetryCount, that.failedRenewalRetryCount) &&
                Objects.equals(failedRenewalRetryDelayHours, that.failedRenewalRetryDelayHours) &&
                Objects.equals(timeOver, that.timeOver) &&
                Objects.equals(active, that.active) &&
                Objects.equals(subscriptionCycleDays, that.subscriptionCycleDays) &&
                Objects.equals(purgeCycleDays, that.purgeCycleDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorPlanId, vendorId, autoRenewalStartTime, failedRenewalRetryCount, failedRenewalRetryDelayHours, timeOver, active, subscriptionCycleDays, purgeCycleDays);
    }
}
