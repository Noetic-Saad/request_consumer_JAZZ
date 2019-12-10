package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "vendor_plan_accounts", schema = "public", catalog = "subscriptiondb")
public
class VendorPlanAccountsEntity {
    private long id;
    private long planId;
    private String clientId;
    private String clientSecret;
    private Timestamp cdatetime;
    private Integer status;
    private Timestamp modifyDatetime;
    private String registeredIpUrl;
    private Integer responseTypeId;
    private Long tokenValidity;

    @Id
    @Column(name = "id")
    public
    long getId() {
        return id;
    }

    public
    void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "plan_id")
    public
    long getPlanId() {
        return planId;
    }

    public
    void setPlanId(long planId) {
        this.planId = planId;
    }

    @Basic
    @Column(name = "client_id")
    public
    String getClientId() {
        return clientId;
    }

    public
    void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Basic
    @Column(name = "client_secret")
    public
    String getClientSecret() {
        return clientSecret;
    }

    public
    void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Basic
    @Column(name = "cdatetime")
    public
    Timestamp getCdatetime() {
        return cdatetime;
    }

    public
    void setCdatetime(Timestamp cdatetime) {
        this.cdatetime = cdatetime;
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
    @Column(name = "modify_datetime")
    public
    Timestamp getModifyDatetime() {
        return modifyDatetime;
    }

    public
    void setModifyDatetime(Timestamp modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    @Basic
    @Column(name = "registered_ip_url")
    public
    String getRegisteredIpUrl() {
        return registeredIpUrl;
    }

    public
    void setRegisteredIpUrl(String registeredIpUrl) {
        this.registeredIpUrl = registeredIpUrl;
    }

    @Basic
    @Column(name = "response_type_id")
    public
    Integer getResponseTypeId() {
        return responseTypeId;
    }

    public
    void setResponseTypeId(Integer responseTypeId) {
        this.responseTypeId = responseTypeId;
    }

    @Basic
    @Column(name = "token_validity")
    public
    Long getTokenValidity() {
        return tokenValidity;
    }

    public
    void setTokenValidity(Long tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VendorPlanAccountsEntity that = (VendorPlanAccountsEntity) o;

        if (id != that.id) return false;
        if (planId != that.planId) return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null) return false;
        if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null) return false;
        if (cdatetime != null ? !cdatetime.equals(that.cdatetime) : that.cdatetime != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (modifyDatetime != null ? !modifyDatetime.equals(that.modifyDatetime) : that.modifyDatetime != null)
            return false;
        if (registeredIpUrl != null ? !registeredIpUrl.equals(that.registeredIpUrl) : that.registeredIpUrl != null)
            return false;
        if (responseTypeId != null ? !responseTypeId.equals(that.responseTypeId) : that.responseTypeId != null)
            return false;
        if (tokenValidity != null ? !tokenValidity.equals(that.tokenValidity) : that.tokenValidity != null)
            return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (planId ^ (planId >>> 32));
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
        result = 31 * result + (cdatetime != null ? cdatetime.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (modifyDatetime != null ? modifyDatetime.hashCode() : 0);
        result = 31 * result + (registeredIpUrl != null ? registeredIpUrl.hashCode() : 0);
        result = 31 * result + (responseTypeId != null ? responseTypeId.hashCode() : 0);
        result = 31 * result + (tokenValidity != null ? tokenValidity.hashCode() : 0);
        return result;
    }
}
