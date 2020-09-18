package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "subscription_message", schema = "public", catalog = "sgw")
public class SubscriptionMessageEntity {
    private long id;
    private Long msisdn;
    private String message;
    private Integer smsStatus;
    private Timestamp cdate;
    private String messageType;


    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "subscription_message_id_seq",sequenceName = "subscription_message_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "subscription_message_id_seq")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "msisdn")
    public Long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Long msisdn) {
        this.msisdn = msisdn;
    }

    @Basic
    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "sms_status")
    public Integer getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(Integer smsStatus) {
        this.smsStatus = smsStatus;
    }

    @Basic
    @Column(name = "cdate")
    public Timestamp getCdate() {
        return cdate;
    }

    public void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Basic
    @Column(name = "message_type")
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionMessageEntity that = (SubscriptionMessageEntity) o;
        return id == that.id &&
                Objects.equals(msisdn, that.msisdn) &&
                Objects.equals(message, that.message) &&
                Objects.equals(smsStatus, that.smsStatus) &&
                Objects.equals(cdate, that.cdate) &&
                Objects.equals(messageType, that.messageType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn, message, smsStatus, cdate, messageType);
    }
}
