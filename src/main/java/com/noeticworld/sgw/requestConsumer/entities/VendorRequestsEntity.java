package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "vendor_req", schema = "public", catalog = "sgw")
public class VendorRequestsEntity {
    private long id;
    private String correlationid;
    private String resultStatus;
    private Boolean isFetched;
    private Date cdatetime;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name="vendor_requests_id_generator", sequenceName = "vendor_requests_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "correlationid")
    public String getCorrelationid() {
        return correlationid;
    }

    public void setCorrelationid(String correlationid) {
        this.correlationid = correlationid;
    }

    @Basic
    @Column(name = "result_status")
    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    @Basic
    @Column(name = "is_fetched")
    public Boolean getFetched() {
        return isFetched;
    }

    public void setFetched(Boolean fetched) {
        isFetched = fetched;
    }

    @Basic
    @Column(name = "cdatetime")
    public Date getCdatetime() {
        return cdatetime;
    }

    public void setCdatetime(Date cdatetime) {
        this.cdatetime = cdatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorRequestsEntity entity = (VendorRequestsEntity) o;
        return id == entity.id &&
                Objects.equals(correlationid, entity.correlationid) &&
                Objects.equals(resultStatus, entity.resultStatus) &&
                Objects.equals(isFetched, entity.isFetched) &&
                Objects.equals(cdatetime, entity.cdatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, correlationid, resultStatus, isFetched, cdatetime);
    }
}
