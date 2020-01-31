package com.noeticworld.sgw.requestConsumer.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "subscription_cycles", schema = "public", catalog = "sgw")
public class SubscriptionCyclesEntity {
    private int id;
    private float days;
    private String label;
    private String description;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "days")
    public float getDays() {
        return days;
    }

    public void setDays(float days) {
        this.days = days;
    }

    @Basic
    @Column(name = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionCyclesEntity that = (SubscriptionCyclesEntity) o;
        return id == that.id &&
                Float.compare(that.days, days) == 0 &&
                Objects.equals(label, that.label) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, days, label, description);
    }
}
