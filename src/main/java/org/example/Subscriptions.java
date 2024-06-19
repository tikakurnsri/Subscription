package org.example;

import org.json.JSONObject;

import java.sql.Date;

public class Subscriptions {
    private int id;
    private int customer;
    private int billingPeriod;
    private String billingPeriodUnit;
    private int totalDue;
    private Date activatedAt;
    private Date currentTermStart;
    private Date currentTermEnd;
    private String status;

    // Getter dan Setter untuk setiap properti
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public int getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(int billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public String getBillingPeriodUnit() {
        return billingPeriodUnit;
    }

    public void setBillingPeriodUnit(String billingPeriodUnit) {
        this.billingPeriodUnit = billingPeriodUnit;
    }

    public int getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(int totalDue) {
        this.totalDue = totalDue;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Date getCurrentTermStart() {
        return currentTermStart;
    }

    public void setCurrentTermStart(Date currentTermStart) {
        this.currentTermStart = currentTermStart;
    }

    public Date getCurrentTermEnd() {
        return currentTermEnd;
    }

    public void setCurrentTermEnd(Date currentTermEnd) {
        this.currentTermEnd = currentTermEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Mengubah objek menjadi JSONObject
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("customer", this.customer);
        json.put("billing_period", this.billingPeriod);
        json.put("billing_period_unit", this.billingPeriodUnit);
        json.put("total_due", this.totalDue);
        json.put("activated_at", this.activatedAt);
        json.put("current_term_start", this.currentTermStart);
        json.put("current_term_end", this.currentTermEnd);
        json.put("status", this.status);
        return json;
    }

    public void setCustomerId(int customerId) {
    }

    public void setStartDate(Date startDate) {
    }

    public void setEndDate(Date endDate) {

    }
}
