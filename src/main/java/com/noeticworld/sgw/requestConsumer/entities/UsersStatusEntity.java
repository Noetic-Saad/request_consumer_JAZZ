package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_status", schema = "public", catalog = "subscriptiondb")
public
class UsersStatusEntity {
    private long id;
    private String msisdn;
    private Long statusId;
    private Timestamp cdate;
    private Integer status;
    private Timestamp modifyDate;
    private Timestamp expiryDatetime;
    private long vendorPlanId;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public
    long getId() {
        return id;
    }

    public
    void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "msisdn")
    public
    String getMsisdn() {
        return msisdn;
    }

    public
    void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "status_id")
    public
    Long getStatusId() {
        return statusId;
    }

    public
    void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    @Basic
    @Column(name = "cdate")
    public
    Timestamp getCdate() {
        return cdate;
    }

    public
    void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Basic
    @Column(name = "status")
    public
    Integer getStatus() {
        return status;
    }

    public
    void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "modify_date")
    public
    Timestamp getModifyDate() {
        return modifyDate;
    }

    public
    void setModifyDate(Timestamp modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Basic
    @Column(name = "expiry_datetime")
    public
    Timestamp getExpiryDatetime() {
        return expiryDatetime;
    }

    public
    void setExpiryDatetime(Timestamp expiryDatetime) {
        this.expiryDatetime = expiryDatetime;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public
    long getVendorPlanId() {
        return vendorPlanId;
    }

    public
    void setVendorPlanId(long vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersStatusEntity that = (UsersStatusEntity) o;

        if (id != that.id) return false;
        if (vendorPlanId != that.vendorPlanId) return false;
        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (statusId != null ? !statusId.equals(that.statusId) : that.statusId != null) return false;
        if (cdate != null ? !cdate.equals(that.cdate) : that.cdate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (modifyDate != null ? !modifyDate.equals(that.modifyDate) : that.modifyDate != null) return false;
        if (expiryDatetime != null ? !expiryDatetime.equals(that.expiryDatetime) : that.expiryDatetime != null)
            return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (msisdn != null ? msisdn.hashCode() : 0);
        result = 31 * result + (statusId != null ? statusId.hashCode() : 0);
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (modifyDate != null ? modifyDate.hashCode() : 0);
        result = 31 * result + (expiryDatetime != null ? expiryDatetime.hashCode() : 0);
        result = 31 * result + (int) (vendorPlanId ^ (vendorPlanId >>> 32));
        return result;
    }
}
