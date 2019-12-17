package com.noeticworld.sgw.util;

import java.util.Date;

public class RequestProperties {

    private long vendorPlanId;
    private long msisdn;
    private String requestAction;
    private String correlationId;
    private Date originDateTime;

    public RequestProperties(CustomMessage customMessage) {
        this.vendorPlanId = Long.parseLong(customMessage.getVendorPlanId());
        this.msisdn = Long.parseLong(customMessage.getMsisdn());
        this.requestAction = customMessage.getAction();
        this.correlationId = customMessage.getCorelationId();
        this.originDateTime = new Date(Long.parseLong(customMessage.getDateTime()));
        System.out.println(this.originDateTime);
    }

    public long getVendorPlanId() {
        return vendorPlanId;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public String getRequestAction() {
        return requestAction;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Date getOriginDateTime() {
        return originDateTime;
    }

    public static void main(String[] args) {
        long current = System.currentTimeMillis();
        System.out.println(new Date(current));
    }
}
