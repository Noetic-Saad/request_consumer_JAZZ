package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "users_status", schema = "public", catalog = "sgw")
public class UsersStatusEntity {
    private long id;
    private Long userId;
    private Integer statusId;
    private Timestamp cdate;
    private Timestamp expiryDatetime;
    private Long vendorPlanId;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "status_id")
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    @Basic
    @Column(name = "cdate")
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Basic
    @Column(name = "expiry_datetime")
    public Timestamp getExpiryDatetime() {
        return expiryDatetime;
    }

    public void setExpiryDatetime(Timestamp expiryDatetime) {
        this.expiryDatetime = expiryDatetime;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public Long getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Long vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersStatusEntity that = (UsersStatusEntity) o;
        return id == that.id &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(statusId, that.statusId) &&
                Objects.equals(cdate, that.cdate) &&
                Objects.equals(expiryDatetime, that.expiryDatetime) &&
                Objects.equals(vendorPlanId, that.vendorPlanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, statusId, cdate, expiryDatetime, vendorPlanId);
    }
}
