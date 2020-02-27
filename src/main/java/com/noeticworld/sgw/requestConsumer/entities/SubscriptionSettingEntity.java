package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "subscription_setting", schema = "public", catalog = "sgw")
public class SubscriptionSettingEntity {
    private long id;
    private Integer vendorId;
    private Time autoRenewalStartTime;
    private Integer failedRenewalRetryCount;
    private Time failedRenewalRetryDelayHours;
    private Time timeOver;
    private Boolean isActive;
    private Integer purgeCycleDays;
    private String expiryTime;
    private long vendorPlanId;
    private Integer subCycleId;
    private Integer operatorId;
    private Boolean weeklyRenwalRetry;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    @Basic
    @Column(name = "purge_cycle_days")
    public Integer getPurgeCycleDays() {
        return purgeCycleDays;
    }

    public void setPurgeCycleDays(Integer purgeCycleDays) {
        this.purgeCycleDays = purgeCycleDays;
    }

    @Basic
    @Column(name = "expiry_time")
    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionSettingEntity that = (SubscriptionSettingEntity) o;
        return id == that.id &&
                Objects.equals(vendorId, that.vendorId) &&
                Objects.equals(autoRenewalStartTime, that.autoRenewalStartTime) &&
                Objects.equals(failedRenewalRetryCount, that.failedRenewalRetryCount) &&
                Objects.equals(failedRenewalRetryDelayHours, that.failedRenewalRetryDelayHours) &&
                Objects.equals(timeOver, that.timeOver) &&
                Objects.equals(isActive, that.isActive) &&
                Objects.equals(purgeCycleDays, that.purgeCycleDays) &&
                Objects.equals(expiryTime, that.expiryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorId, autoRenewalStartTime, failedRenewalRetryCount, failedRenewalRetryDelayHours, timeOver, isActive, purgeCycleDays, expiryTime);
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
    @Column(name = "sub_cycle_id")
    public Integer getSubCycleId() {
        return subCycleId;
    }

    public void setSubCycleId(Integer subCycleId) {
        this.subCycleId = subCycleId;
    }

    @Basic
    @Column(name = "operator_id")
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Basic
    @Column(name = "weekly_renwal_retry")
    public Boolean getWeeklyRenwalRetry() {
        return weeklyRenwalRetry;
    }

    public void setWeeklyRenwalRetry(Boolean weeklyRenwalRetry) {
        this.weeklyRenwalRetry = weeklyRenwalRetry;
    }
}
