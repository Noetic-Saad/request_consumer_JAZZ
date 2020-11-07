package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "login", schema = "public", catalog = "sgw")
public class LoginEntity {
    private Integer id;
    private Long msisdn;
    @Basic
    @Column(name = "updateddate")
    public Timestamp getUpdateddate() {
        return updateddate;
    }

    public void setUpdateddate(Timestamp updateddate) {
        this.updateddate = updateddate;
    }

    private Timestamp updateddate;
    @Basic
    @Column(name = "msisdn")
    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "tbl_login_id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




}
