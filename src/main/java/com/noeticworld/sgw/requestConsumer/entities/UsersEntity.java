package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users", schema = "public", catalog = "subscriptiondb")
public
class UsersEntity {
    private long id;
    private String msisdn;
    private Timestamp cdate;

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
    @Column(name = "msisdn")
    public
    String getMsisdn() {
        return msisdn;
    }

    public
    void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "cdate")
    public
    Timestamp getCdate() {
        return cdate;
    }

    public
    void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (id != that.id) return false;
        if (msisdn != null ? !msisdn.equals(that.msisdn) : that.msisdn != null) return false;
        if (cdate != null ? !cdate.equals(that.cdate) : that.cdate != null) return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (msisdn != null ? msisdn.hashCode() : 0);
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        return result;
    }
}
