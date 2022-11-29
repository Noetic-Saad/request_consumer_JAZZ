package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "otp_records", schema = "public", catalog = "sgw")
public class OtpRecordsEntity implements Serializable {
    private long id;
    private Long msisdn;
    private Long vendorPlanId;
    private Integer otpNumber;
    private Timestamp cdate;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "otp_records_seq",sequenceName = "otp_records_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "otp_records_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "msisdn")
    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
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
    @Column(name = "otp_number")
    public Integer getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(Integer otpNumber) {
        this.otpNumber = otpNumber;
    }

    @Basic
    @Column(name = "cdate")
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtpRecordsEntity that = (OtpRecordsEntity) o;
        return id == that.id &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(otpNumber, that.otpNumber) &&
                Objects.equals(cdate, that.cdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn, vendorPlanId, otpNumber, cdate);
    }
}
