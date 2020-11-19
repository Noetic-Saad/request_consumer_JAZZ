package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "login_records", schema = "public", catalog = "sgw")
public class LoginRecordsEntity {
    private long id;
    private Long msisdn;
    private Long vendorPlanId;
    private String sessionId;
    private Timestamp ctime;
    private String remoteServerIp;
    private String localServerIp;
    private Boolean isAcitve;
    private Long sessionTime;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "login_records_seq",sequenceName = "login_records_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "login_records_seq")
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
    @Column(name = "session_id")
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Basic
    @Column(name = "ctime")
    public Timestamp getCtime() {
        return ctime;
    }

    public void setCtime(Timestamp ctime) {
        this.ctime = ctime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRecordsEntity that = (LoginRecordsEntity) o;
        return id == that.id &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(ctime, that.ctime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn, vendorPlanId, sessionId, ctime);
    }

    @Basic
    @Column(name = "remote_server_ip")
    public String getRemoteServerIp() {
        return remoteServerIp;
    }

    public void setRemoteServerIp(String remoteServerIp) {
        this.remoteServerIp = remoteServerIp;
    }

    @Basic
    @Column(name = "local_server_ip")
    public String getLocalServerIp() {
        return localServerIp;
    }

    public void setLocalServerIp(String localServerIp) {
        this.localServerIp = localServerIp;
    }

    @Basic
    @Column(name = "is_acitve")
    public Boolean getAcitve() {
        return isAcitve;
    }

    public void setAcitve(Boolean acitve) {
        isAcitve = acitve;
    }

    @Basic
    @Column(name = "session_time")
    public Long getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Long sessionTime) {
        this.sessionTime = sessionTime;
    }
}
