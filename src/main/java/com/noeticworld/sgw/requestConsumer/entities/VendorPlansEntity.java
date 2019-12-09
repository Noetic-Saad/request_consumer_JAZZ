package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "vendor_plans", schema = "public", catalog = "subscriptiondb")
public
class VendorPlansEntity {
    private long id;
    private long vendorId;
    private long serviceId;
    private Double pricePoint;
    private Long shortcode;
    private Timestamp cdate;
    private Integer status;
    private Long validityDays;
    private long vendorCategoryId;
    private long operatorId;

    @Id
    @Column(name = "id")
    public
    long getId() {
        return id;
    }

    public
    void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vendor_id")
    public
    long getVendorId() {
        return vendorId;
    }

    public
    void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    @Basic
    @Column(name = "service_id")
    public
    long getServiceId() {
        return serviceId;
    }

    public
    void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    @Basic
    @Column(name = "price_point")
    public
    Double getPricePoint() {
        return pricePoint;
    }

    public
    void setPricePoint(Double pricePoint) {
        this.pricePoint = pricePoint;
    }

    @Basic
    @Column(name = "shortcode")
    public
    Long getShortcode() {
        return shortcode;
    }

    public
    void setShortcode(Long shortcode) {
        this.shortcode = shortcode;
    }

    @Basic
    @Column(name = "cdate")
    public
    Timestamp getCdate() {
        return cdate;
    }

    public
    void setCdate(Timestamp cdate) {
        this.cdate = cdate;
    }

    @Basic
    @Column(name = "status")
    public
    Integer getStatus() {
        return status;
    }

    public
    void setStatus(Integer status) {
        this.status = status;
    }

    @Basic
    @Column(name = "validity_days")
    public
    Long getValidityDays() {
        return validityDays;
    }

    public
    void setValidityDays(Long validityDays) {
        this.validityDays = validityDays;
    }

    @Basic
    @Column(name = "vendor_category_id")
    public
    long getVendorCategoryId() {
        return vendorCategoryId;
    }

    public
    void setVendorCategoryId(long vendorCategoryId) {
        this.vendorCategoryId = vendorCategoryId;
    }

    @Basic
    @Column(name = "operator_id")
    public
    long getOperatorId() {
        return operatorId;
    }

    public
    void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public
    boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VendorPlansEntity that = (VendorPlansEntity) o;

        if (id != that.id) return false;
        if (vendorId != that.vendorId) return false;
        if (serviceId != that.serviceId) return false;
        if (vendorCategoryId != that.vendorCategoryId) return false;
        if (operatorId != that.operatorId) return false;
        if (pricePoint != null ? !pricePoint.equals(that.pricePoint) : that.pricePoint != null) return false;
        if (shortcode != null ? !shortcode.equals(that.shortcode) : that.shortcode != null) return false;
        if (cdate != null ? !cdate.equals(that.cdate) : that.cdate != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (validityDays != null ? !validityDays.equals(that.validityDays) : that.validityDays != null) return false;

        return true;
    }

    @Override
    public
    int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (vendorId ^ (vendorId >>> 32));
        result = 31 * result + (int) (serviceId ^ (serviceId >>> 32));
        result = 31 * result + (pricePoint != null ? pricePoint.hashCode() : 0);
        result = 31 * result + (shortcode != null ? shortcode.hashCode() : 0);
        result = 31 * result + (cdate != null ? cdate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (validityDays != null ? validityDays.hashCode() : 0);
        result = 31 * result + (int) (vendorCategoryId ^ (vendorCategoryId >>> 32));
        result = 31 * result + (int) (operatorId ^ (operatorId >>> 32));
        return result;
    }
}
