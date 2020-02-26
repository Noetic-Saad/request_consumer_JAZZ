package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "mt_message_settings", schema = "public", catalog = "sgw")
public class MtMessageSettingsEntity {
    private int id;
    private String serviceUsername;
    private String servicePassword;
    private Integer serviceId;
    private String shortCode;
    private Integer vendorPlanId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "service_username")
    public String getServiceUsername() {
        return serviceUsername;
    }

    public void setServiceUsername(String serviceUsername) {
        this.serviceUsername = serviceUsername;
    }

    @Basic
    @Column(name = "service_password")
    public String getServicePassword() {
        return servicePassword;
    }

    public void setServicePassword(String servicePassword) {
        this.servicePassword = servicePassword;
    }

    @Basic
    @Column(name = "service_id")
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "short_code")
    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public Integer getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Integer vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MtMessageSettingsEntity that = (MtMessageSettingsEntity) o;
        return id == that.id &&
                Objects.equals(serviceUsername, that.serviceUsername) &&
                Objects.equals(servicePassword, that.servicePassword) &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(shortCode, that.shortCode) &&
                Objects.equals(vendorPlanId, that.vendorPlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceUsername, servicePassword, serviceId, shortCode, vendorPlanId);
    }
}
