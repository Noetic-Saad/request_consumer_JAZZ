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
    private boolean isFromEDA;
    private FiegnResponse fiegnResponse;

    public RequestProperties() {
        // Default constructor
    }

    public RequestProperties(CustomMessage customMessage) {
        this.vendorPlanId = Long.parseLong(customMessage.getVendorPlanId());
        this.msisdn = Long.parseLong(customMessage.getMsisdn());
        this.requestAction = customMessage.getAction();
        this.correlationId = customMessage.getCorelationId();
        this.originDateTime = new Date(Long.parseLong(customMessage.getDateTime()));
        this.trackerId = customMessage.getTrackerId();
        this.otp = customMessage.isOtp();
        this.isFromEDA = false;
        this.fiegnResponse = null;

        if (customMessage.getSessionId() != null) this.sessionId = customMessage.getSessionId();
        if (customMessage.getRemoteServerIp() != null) this.remoteServerIp = customMessage.getRemoteServerIp();
        if (customMessage.getLocalServerIp() != null) this.localServerIp = customMessage.getLocalServerIp();
        if (customMessage.isOtp()) {
            this.otpNumber = customMessage.getOtpNumber();
        }
    }

    public String getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Date getOriginDateTime() {
        return originDateTime;
    }

    public void setOriginDateTime(Date originDateTime) {
        this.originDateTime = originDateTime;
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

    public long getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(long vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public boolean isFromEDA() {
        return isFromEDA;
    }

    public void setFromEDA(boolean fromEDA) {
        isFromEDA = fromEDA;
    }

    public FiegnResponse getFiegnResponse() {
        return fiegnResponse;
    }

    public void setFiegnResponse(FiegnResponse fiegnResponse) {
        this.fiegnResponse = fiegnResponse;
    }
}
