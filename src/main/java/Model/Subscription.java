package Model;

import org.json.JSONObject;
import java.util.List;

public class Subscription {
    private int id;
    private int customers;
    private int billing_period;
    private String billing_period_unit;
    private int total_due;
    private String activated_at;
    private String current_term_start;
    private String current_term_end;
    private String status;
    private List<SubscriptionItem> items; // List of SubscriptionItem

    // Constructors
    public Subscription() {}

    public Subscription(int id, int customerId, int billingPeriod, String billingPeriodUnit, int totalDue,
                        String activatedAt, String currentTermStart, String currentTermEnd, String status,
                        List<SubscriptionItem> items) {
        this.id = id;
        this.customers = customerId;
        this.billing_period = billingPeriod;
        this.billing_period_unit = billingPeriodUnit;
        this.total_due = totalDue;
        this.activated_at = activatedAt;
        this.current_term_start = currentTermStart;
        this.current_term_end = currentTermEnd;
        this.status = status;
        this.items = items;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customers; }
    public void setCustomerId(int customerId) { this.customers = customerId; }

    public int getBillingPeriod() { return billing_period; }
    public void setBillingPeriod(int billingPeriod) { this.billing_period = billingPeriod; }

    public String getBillingPeriodUnit() { return billing_period_unit; }
    public void setBillingPeriodUnit(String billingPeriodUnit) { this.billing_period_unit = billingPeriodUnit; }

    public int getTotalDue() { return total_due; }
    public void setTotalDue(int totalDue) { this.total_due = totalDue; }

    public String getActivatedAt() { return activated_at; }
    public void setActivatedAt(String activatedAt) { this.activated_at = activatedAt; }

    public String getCurrentTermStart() { return current_term_start; }
    public void setCurrentTermStart(String currentTermStart) { this.current_term_start = currentTermStart; }

    public String getCurrentTermEnd() { return current_term_end; }
    public void setCurrentTermEnd(String currentTermEnd) { this.current_term_end = currentTermEnd; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<SubscriptionItem> getItems() { return items; }
    public void setItems(List<SubscriptionItem> items) { this.items = items; }

    // Override toString() method to represent the object as JSON
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("customerId", customers);
        jsonObject.put("billingPeriod", billing_period);
        jsonObject.put("billingPeriodUnit", billing_period_unit);
        jsonObject.put("totalDue", total_due);
        jsonObject.put("activatedAt", activated_at != null ? activated_at.toString() : null);
        jsonObject.put("currentTermStart", current_term_start != null ? current_term_start.toString() : null);
        jsonObject.put("currentTermEnd", current_term_end != null ? current_term_end.toString() : null);
        jsonObject.put("status", status);
        jsonObject.put("items", items);
        return jsonObject.toString();
    }
}
