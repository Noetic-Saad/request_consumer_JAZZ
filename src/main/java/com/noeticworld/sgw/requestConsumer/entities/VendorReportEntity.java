package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "vendor_report", schema = "public", catalog = "sgw")
public class VendorReportEntity {
    private int id;
    private Integer venodorPlanId;
    private String trackerId;
    private long msisdn;
    private Timestamp cdate;
    private Integer postbackSent;
    private Integer operatorId;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "vendor_report_seq",sequenceName = "vendor_report_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "vendor_report_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "venodor_plan_id")
    public Integer getVenodorPlanId() {
        return venodorPlanId;
    }

    public void setVenodorPlanId(Integer venodorPlanId) {
        this.venodorPlanId = venodorPlanId;
    }

    @Basic
    @Column(name = "tracker_id")
    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
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
        VendorReportEntity that = (VendorReportEntity) o;
        return id == that.id &&
                msisdn == that.msisdn &&
                Objects.equals(venodorPlanId, that.venodorPlanId) &&
                Objects.equals(trackerId, that.trackerId) &&
                Objects.equals(cdate, that.cdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, venodorPlanId, trackerId, msisdn, cdate);
    }

    @Basic
    @Column(name = "postback_sent")
    public Integer getPostbackSent() {
        return postbackSent;
    }

    public void setPostbackSent(Integer postbackSent) {
        this.postbackSent = postbackSent;
    }

    @Basic
    @Column(name = "operator_id")
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
}
