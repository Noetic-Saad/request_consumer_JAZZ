package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "msisdn_correlations", schema = "public", catalog = "sgw")
public class MsisdnCorrelations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long msisdn;
    private String correlationId;
    private Timestamp cdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }
}
