package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "vendor_plan_accounts", schema = "public", catalog = "sgw")
public class VendorPlanAccountsEntity {
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
    private String grantTypes;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "plan_id")
    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    @Basic
    @Column(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Basic
    @Column(name = "client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Basic
    @Column(name = "cdatetime")
    public Timestamp getCdatetime() {
        return cdatetime;
    }

    public void setCdatetime(Timestamp cdatetime) {
        this.cdatetime = cdatetime;
    }

    @Basic
    @Column(name = "status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "modify_datetime")
    public Timestamp getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(Timestamp modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    @Basic
    @Column(name = "registered_ip_url")
    public String getRegisteredIpUrl() {
        return registeredIpUrl;
    }

    public void setRegisteredIpUrl(String registeredIpUrl) {
        this.registeredIpUrl = registeredIpUrl;
    }

    @Basic
    @Column(name = "response_type_id")
    public Integer getResponseTypeId() {
        return responseTypeId;
    }

    public void setResponseTypeId(Integer responseTypeId) {
        this.responseTypeId = responseTypeId;
    }

    @Basic
    @Column(name = "token_validity")
    public Long getTokenValidity() {
        return tokenValidity;
    }

    public void setTokenValidity(Long tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    @Basic
    @Column(name = "grant_types")
    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorPlanAccountsEntity that = (VendorPlanAccountsEntity) o;
        return id == that.id &&
                planId == that.planId &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(clientSecret, that.clientSecret) &&
                Objects.equals(cdatetime, that.cdatetime) &&
                Objects.equals(status, that.status) &&
                Objects.equals(modifyDatetime, that.modifyDatetime) &&
                Objects.equals(registeredIpUrl, that.registeredIpUrl) &&
                Objects.equals(responseTypeId, that.responseTypeId) &&
                Objects.equals(tokenValidity, that.tokenValidity) &&
                Objects.equals(grantTypes, that.grantTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, planId, clientId, clientSecret, cdatetime, status, modifyDatetime, registeredIpUrl, responseTypeId, tokenValidity, grantTypes);
    }
}
