package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "vendor_account_access", schema = "public", catalog = "sgw")
public class VendorAccountAccessEntity {
    private long id;
    private String accessToken;
    private Timestamp accessTokenCdatetime;
    private Timestamp accessTokenExpirytime;
    private String authentication;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Basic
    @Column(name = "access_token_cdatetime")
    public Timestamp getAccessTokenCdatetime() {
        return accessTokenCdatetime;
    }

    public void setAccessTokenCdatetime(Timestamp accessTokenCdatetime) {
        this.accessTokenCdatetime = accessTokenCdatetime;
    }

    @Basic
    @Column(name = "access_token_expirytime")
    public Timestamp getAccessTokenExpirytime() {
        return accessTokenExpirytime;
    }

    public void setAccessTokenExpirytime(Timestamp accessTokenExpirytime) {
        this.accessTokenExpirytime = accessTokenExpirytime;
    }

    @Basic
    @Column(name = "authentication")
    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorAccountAccessEntity that = (VendorAccountAccessEntity) o;
        return id == that.id &&
                Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(accessTokenCdatetime, that.accessTokenCdatetime) &&
                Objects.equals(accessTokenExpirytime, that.accessTokenExpirytime) &&
                Objects.equals(authentication, that.authentication);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accessToken, accessTokenCdatetime, accessTokenExpirytime, authentication);
    }
}
