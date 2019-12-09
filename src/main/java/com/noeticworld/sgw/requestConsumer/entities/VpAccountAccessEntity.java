package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "vp_account_access", schema = "public", catalog = "subscriptiondb")
public
class VpAccountAccessEntity {
    private long id;
    private String accessToken;
    private Timestamp accessTokenCdatetime;
    private Timestamp accessTokenExpirytime;
    private Integer vpAccountId;
    private String authentication;

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
    @Column(name = "access_token")
    public
    String getAccessToken() {
        return accessToken;
    }

    public
    void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Basic
    @Column(name = "access_token_cdatetime")
    public
    Timestamp getAccessTokenCdatetime() {
        return accessTokenCdatetime;
    }

    public
    void setAccessTokenCdatetime(Timestamp accessTokenCdatetime) {
        this.accessTokenCdatetime = accessTokenCdatetime;
    }

    @Basic
    @Column(name = "access_token_expirytime")
    public
    Timestamp getAccessTokenExpirytime() {
        return accessTokenExpirytime;
    }

    public
    void setAccessTokenExpirytime(Timestamp accessTokenExpirytime) {
        this.accessTokenExpirytime = accessTokenExpirytime;
    }

    @Basic
    @Column(name = "vp_account_id")
    public
    Integer getVpAccountId() {
        return vpAccountId;
    }

    public
    void setVpAccountId(Integer vpAccountId) {
        this.vpAccountId = vpAccountId;
    }

    @Basic
    @Column(name = "authentication")
    public
    String getAuthentication() {
        return authentication;
    }

    public
    void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VpAccountAccessEntity that = (VpAccountAccessEntity) o;

        if (id != that.id) return false;
        if (accessToken != null ? !accessToken.equals(that.accessToken) : that.accessToken != null) return false;
        if (accessTokenCdatetime != null ? !accessTokenCdatetime.equals(that.accessTokenCdatetime) : that.accessTokenCdatetime != null)
            return false;
        if (accessTokenExpirytime != null ? !accessTokenExpirytime.equals(that.accessTokenExpirytime) : that.accessTokenExpirytime != null)
            return false;
        if (vpAccountId != null ? !vpAccountId.equals(that.vpAccountId) : that.vpAccountId != null) return false;
        if (authentication != null ? !authentication.equals(that.authentication) : that.authentication != null)
            return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (accessTokenCdatetime != null ? accessTokenCdatetime.hashCode() : 0);
        result = 31 * result + (accessTokenExpirytime != null ? accessTokenExpirytime.hashCode() : 0);
        result = 31 * result + (vpAccountId != null ? vpAccountId.hashCode() : 0);
        result = 31 * result + (authentication != null ? authentication.hashCode() : 0);
        return result;
    }
}
