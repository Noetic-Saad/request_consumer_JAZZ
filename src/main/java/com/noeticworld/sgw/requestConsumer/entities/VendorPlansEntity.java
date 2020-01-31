package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "vendor_plans", schema = "public", catalog = "sgw")
public class VendorPlansEntity {
    private long id;
    private long vendorId;
    private Long serviceId;
    private Double pricePoint;
    private Timestamp cdate;
    private Long validityDays;
    private long vendorCategoryId;
    private Float taxAmount;
    private Boolean active;
    private Timestamp startDate;
    private Integer subCycle;
    private Integer operatorId;
    private Integer mtResponse;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vendor_id")
    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    @Basic
    @Column(name = "service_id")
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "price_point")
    public Double getPricePoint() {
        return pricePoint;
    }

    public void setPricePoint(Double pricePoint) {
        this.pricePoint = pricePoint;
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
    @Column(name = "validity_days")
    public Long getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Long validityDays) {
        this.validityDays = validityDays;
    }

    @Basic
    @Column(name = "vendor_category_id")
    public long getVendorCategoryId() {
        return vendorCategoryId;
    }

    public void setVendorCategoryId(long vendorCategoryId) {
        this.vendorCategoryId = vendorCategoryId;
    }

    @Basic
    @Column(name = "tax_amount")
    public Float getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Float taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Basic
    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Basic
    @Column(name = "start_date")
    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorPlansEntity that = (VendorPlansEntity) o;
        return id == that.id &&
                vendorId == that.vendorId &&
                vendorCategoryId == that.vendorCategoryId &&
                Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(pricePoint, that.pricePoint) &&
                Objects.equals(cdate, that.cdate) &&
                Objects.equals(validityDays, that.validityDays) &&
                Objects.equals(taxAmount, that.taxAmount) &&
                Objects.equals(active, that.active) &&
                Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vendorId, serviceId, pricePoint, cdate, validityDays, vendorCategoryId, taxAmount, active, startDate);
    }

    @Basic
    @Column(name = "sub_cycle")
    public Integer getSubCycle() {
        return subCycle;
    }

    public void setSubCycle(Integer subCycle) {
        this.subCycle = subCycle;
    }

    @Basic
    @Column(name = "operator_id")
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Basic
    @Column(name = "mt_response")
    public Integer getMtResponse() {
        return mtResponse;
    }

    public void setMtResponse(Integer mtResponse) {
        this.mtResponse = mtResponse;
    }
}
