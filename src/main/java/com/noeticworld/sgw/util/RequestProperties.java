package com.noeticworld.sgw.util;

import java.util.Date;

public class RequestProperties {

    private long vendorPlanId;
    private long msisdn;
    private String requestAction;
    private String correlationId;
    private String trackerId;
    private Date originDateTime;
    private boolean otp;
    private long otpNumber;
    private String sessionId;
    private String remoteServerIp;
    private String localServerIp;

    public RequestProperties(CustomMessage customMessage) {
        this.vendorPlanId = Long.parseLong(customMessage.getVendorPlanId());
        this.msisdn = Long.parseLong(customMessage.getMsisdn());
        this.requestAction = customMessage.getAction();
        this.correlationId = customMessage.getCorelationId();
        this.originDateTime = new Date(Long.parseLong(customMessage.getDateTime()));
        this.trackerId = customMessage.getTrackerId();
        if(customMessage.getSessionId()!=null) this.sessionId = customMessage.getSessionId();
        if(customMessage.getRemoteServerIp()!=null) this.remoteServerIp = customMessage.getRemoteServerIp();
        if(customMessage.getLocalServerIp()!=null) this.localServerIp = customMessage.getLocalServerIp();
        this.otp = customMessage.isOtp();
        if(customMessage.isOtp()) {
            this.otpNumber = customMessage.getOtpNumber();
        }
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

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }


    public boolean isOtp() {
        return otp;
    }

    public void setOtp(boolean otp) {
        this.otp = otp;
    }

    public long getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(long otpNumber) {
        this.otpNumber = otpNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemoteServerIp() {
        return remoteServerIp;
    }

    public void setRemoteServerIp(String remoteServerIp) {
        this.remoteServerIp = remoteServerIp;
    }

    public String getLocalServerIp() {
        return localServerIp;
    }

    public void setLocalServerIp(String localServerIp) {
        this.localServerIp = localServerIp;
    }

    public static void main(String[] args) {
        long current = System.currentTimeMillis();
        System.out.println(new Date(current));
    }
}
