package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "sms", schema = "public", catalog = "sgw")
public class SmsEntity {
    private int id;
    private Long msisdn;
    private Integer vendorPlanId;
    private Timestamp sendDate;
    private Integer msgType;
    private String msgText;

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "sms_id_seq",sequenceName = "sms_id_seq",allocationSize=1, initialValue=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sms_id_seq")
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    @Column(name = "send_date")
    public Timestamp getSendDate() {
        return sendDate;
    }

    public void setSendDate(Timestamp sendDate) {
        this.sendDate = sendDate;
    }

    @Basic
    @Column(name = "msg_type")
    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    @Basic
    @Column(name = "msg_text")
    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmsEntity smsEntity = (SmsEntity) o;
        return id == smsEntity.id &&
                Objects.equals(msisdn, smsEntity.msisdn) &&
                Objects.equals(vendorPlanId, smsEntity.vendorPlanId) &&
                Objects.equals(sendDate, smsEntity.sendDate) &&
                Objects.equals(msgType, smsEntity.msgType) &&
                Objects.equals(msgText, smsEntity.msgText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, msisdn, vendorPlanId, sendDate, msgType, msgText);
    }
}
