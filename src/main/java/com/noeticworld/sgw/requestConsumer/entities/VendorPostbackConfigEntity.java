package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "vendor_postback_config", schema = "public", catalog = "sgw")
public class VendorPostbackConfigEntity {
    private int id;
    private Integer vendorPlanId;
    private String url;
    private String param1Name;
    private String param1Value;
    private String param2Name;
    private String param2Value;
    private String param3Name;
    private String param3Value;
    private String param4Name;
    private String param4Value;
    private String param5Name;
    private String param5Value;
    private String param6Name;
    private String param6Value;
    private String param7Name;
    private String param7Value;
    private String param8Name;
    private String param8Value;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vendor_plan_id")
    public Integer getVendorPlanId() {
        return vendorPlanId;
    }

    public void setVendorPlanId(Integer vendorPlanId) {
        this.vendorPlanId = vendorPlanId;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "param1_name")
    public String getParam1Name() {
        return param1Name;
    }

    public void setParam1Name(String param1Name) {
        this.param1Name = param1Name;
    }

    @Basic
    @Column(name = "param1_value")
    public String getParam1Value() {
        return param1Value;
    }

    public void setParam1Value(String param1Value) {
        this.param1Value = param1Value;
    }

    @Basic
    @Column(name = "param2_name")
    public String getParam2Name() {
        return param2Name;
    }

    public void setParam2Name(String param2Name) {
        this.param2Name = param2Name;
    }

    @Basic
    @Column(name = "param2_value")
    public String getParam2Value() {
        return param2Value;
    }

    public void setParam2Value(String param2Value) {
        this.param2Value = param2Value;
    }

    @Basic
    @Column(name = "param3_name")
    public String getParam3Name() {
        return param3Name;
    }

    public void setParam3Name(String param3Name) {
        this.param3Name = param3Name;
    }

    @Basic
    @Column(name = "param3_value")
    public String getParam3Value() {
        return param3Value;
    }

    public void setParam3Value(String param3Value) {
        this.param3Value = param3Value;
    }

    @Basic
    @Column(name = "param4_name")
    public String getParam4Name() {
        return param4Name;
    }

    public void setParam4Name(String param4Name) {
        this.param4Name = param4Name;
    }

    @Basic
    @Column(name = "param4_value")
    public String getParam4Value() {
        return param4Value;
    }

    public void setParam4Value(String param4Value) {
        this.param4Value = param4Value;
    }

    @Basic
    @Column(name = "param5_name")
    public String getParam5Name() {
        return param5Name;
    }

    public void setParam5Name(String param5Name) {
        this.param5Name = param5Name;
    }

    @Basic
    @Column(name = "param5_value")
    public String getParam5Value() {
        return param5Value;
    }

    public void setParam5Value(String param5Value) {
        this.param5Value = param5Value;
    }

    @Basic
    @Column(name = "param6_name")
    public String getParam6Name() {
        return param6Name;
    }

    public void setParam6Name(String param6Name) {
        this.param6Name = param6Name;
    }

    @Basic
    @Column(name = "param6_value")
    public String getParam6Value() {
        return param6Value;
    }

    public void setParam6Value(String param6Value) {
        this.param6Value = param6Value;
    }

    @Basic
    @Column(name = "param7_name")
    public String getParam7Name() {
        return param7Name;
    }

    public void setParam7Name(String param7Name) {
        this.param7Name = param7Name;
    }

    @Basic
    @Column(name = "param7_value")
    public String getParam7Value() {
        return param7Value;
    }

    public void setParam7Value(String param7Value) {
        this.param7Value = param7Value;
    }

    @Basic
    @Column(name = "param8_name")
    public String getParam8Name() {
        return param8Name;
    }

    public void setParam8Name(String param8Name) {
        this.param8Name = param8Name;
    }

    @Basic
    @Column(name = "param8_value")
    public String getParam8Value() {
        return param8Value;
    }

    public void setParam8Value(String param8Value) {
        this.param8Value = param8Value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorPostbackConfigEntity that = (VendorPostbackConfigEntity) o;
        return id == that.id &&
                Objects.equals(vendorPlanId, that.vendorPlanId) &&
                Objects.equals(url, that.url) &&
                Objects.equals(param1Name, that.param1Name) &&
                Objects.equals(param1Value, that.param1Value) &&
                Objects.equals(param2Name, that.param2Name) &&
                Objects.equals(param2Value, that.param2Value) &&
                Objects.equals(param3Name, that.param3Name) &&
                Objects.equals(param3Value, that.param3Value) &&
                Objects.equals(param4Name, that.param4Name) &&
                Objects.equals(param4Value, that.param4Value) &&
                Objects.equals(param5Name, that.param5Name) &&
                Objects.equals(param5Value, that.param5Value) &&
                Objects.equals(param6Name, that.param6Name) &&
                Objects.equals(param6Value, that.param6Value) &&
                Objects.equals(param7Name, that.param7Name) &&
                Objects.equals(param7Value, that.param7Value) &&
                Objects.equals(param8Name, that.param8Name) &&
                Objects.equals(param8Value, that.param8Value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorPlanId, url, param1Name, param1Value, param2Name, param2Value, param3Name, param3Value, param4Name, param4Value, param5Name, param5Value, param6Name, param6Value, param7Name, param7Value, param8Name, param8Value);
    }
}
