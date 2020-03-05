package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "public", catalog = "sgw")
public class UsersEntity {
    private long id;
    private long vendorPlanId;
    private long msisdn;
    private Date cdate;
    private Integer userStatusId;
    private Long operatorId;
    private Integer isOtpVerifired;
    private String trackerId;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "users_id_seq",sequenceName = "users_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "users_id_seq")
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
    @Column(name = "msisdn")
    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "cdate")
    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsersEntity that = (UsersEntity) o;
        return msisdn == that.msisdn &&
                id == that.id &&
                Objects.equals(cdate, that.cdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msisdn, cdate, id);
    }

    @Basic
    @Column(name = "user_status_id")
    public Integer getUserStatusId() {
        return userStatusId;
    }

    public void setUserStatusId(Integer userStatusId) {
        this.userStatusId = userStatusId;
    }

    @Basic
    @Column(name = "operator_id")
    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Basic
    @Column(name = "is_otp_verifired")
    public Integer getIsOtpVerifired() {
        return isOtpVerifired;
    }

    public void setIsOtpVerifired(Integer isOtpVerifired) {
        this.isOtpVerifired = isOtpVerifired;
    }

    @Basic
    @Column(name = "tracker_id")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }
}
