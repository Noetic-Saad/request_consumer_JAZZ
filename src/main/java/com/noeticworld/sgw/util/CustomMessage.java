package com.noeticworld.sgw.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public final class CustomMessage implements Serializable {

    private String text;
    private String msisdn;
    private String action;
    private Timestamp dateTime;
    private String corelationId;
    private String vendorPlanId;

    public CustomMessage(@JsonProperty("text") String text,
                         @JsonProperty("msisdn") String msisdn,
                         @JsonProperty("action") String action,
                        // @JsonProperty("dateTime") Timestamp localDateTime,
                         @JsonProperty("corelationId") String corelationId,
                         @JsonProperty("vendorPlanId") String vendorPlanId) {
        this.text = text;
        this.msisdn = msisdn;
        this.action = action;
        //this.dateTime = localDateTime;
        this.corelationId = corelationId;
        this.vendorPlanId = vendorPlanId;
    }

    @Override
    public String toString() {
        return String.format("Text: %s, MSISDN: %s, Action: %s, TimeStamp: %s, CorelationId: %s, VendorPlan: %s",
                text, msisdn, dateTime, action, corelationId, vendorPlanId);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public String getCorelationId() {
        return corelationId;
    }

    public void setCorelationId(String corelationId) {
        this.corelationId = corelationId;
    }

    public String getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(String vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }
}
