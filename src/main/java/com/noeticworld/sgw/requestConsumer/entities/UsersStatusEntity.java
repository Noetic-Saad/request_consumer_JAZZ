package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "users_status", schema = "public", catalog = "sgw")
public class UsersStatusEntity {
    private long id;
    private Integer statusId;
    private Timestamp cdate;
    private Timestamp expiryDatetime;
    private Integer attempts;
    private Long userId;
    private Long vendorPlanId;
    private Integer subCycleId;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "users_status_id_seq",sequenceName = "users_status_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "users_status_id_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "attempts")
    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersStatusEntity entity = (UsersStatusEntity) o;
        return id == entity.id &&
                Objects.equals(statusId, entity.statusId) &&
                Objects.equals(cdate, entity.cdate) &&
                Objects.equals(expiryDatetime, entity.expiryDatetime) &&
                Objects.equals(attempts, entity.attempts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusId, cdate, expiryDatetime, attempts);
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
    @Column(name = "vendor_plan_id")
    public Long getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Long vendorPlanId) {
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
}
