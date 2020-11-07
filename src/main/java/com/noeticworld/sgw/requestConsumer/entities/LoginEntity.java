package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "login", schema = "public", catalog = "sgw")
public class LoginEntity {
    private Integer id;
    private String msisdn;
    @Basic
    @Column(name = "updateddate")
    public String getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(String updateddate) {
        this.updateddate = updateddate;
    }

    private String updateddate;
    @Basic
    @Column(name = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Id
    @Basic
    @Column(name = "id")
    @SequenceGenerator(name = "tbl_login_id",sequenceName = "tbl_login_id",allocationSize=1, initialValue=1)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




}
