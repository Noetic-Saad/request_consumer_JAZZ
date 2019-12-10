package com.noeticworld.sgw.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

public final class CustomMessage implements Serializable {

    private String text = "";
    private String msisdn;
    private String action;
    private String dateTime;
    private String corelationId;
    private String vendorPlanId;

    @Override
    public String toString() {
        return String.format("Text: %s, MSISDN: %s, Action: %s, DateTime: %s, CorelationId: %s, VendorPlan: %s",
                text, msisdn, action, dateTime, corelationId, vendorPlanId);
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
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
