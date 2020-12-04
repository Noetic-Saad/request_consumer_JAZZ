package com.noeticworld.sgw.util;

public class RequestPropertiesCheckBalance {
    public long msisdn;
    public int operatorId;

    public RequestPropertiesCheckBalance() {
    }

    @Override
    public String toString() {
        return "RequestPropertiesCheckBalance{" +
                "msisdn=" + msisdn +
                ", operatorId='" + operatorId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String transactionId;

}
