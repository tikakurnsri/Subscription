package org.example;

import org.json.JSONObject;

public class ShippingAddresses {
    private int id;
    private int customerId;
    private String title;
    private String line1;
    private String line2;
    private String city;
    private String province;
    private String postcode;

    // Getter dan Setter untuk setiap properti
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    // Mengubah objek menjadi JSONObject
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("customer_id", this.customerId);
        json.put("title", this.title);
        json.put("line1", this.line1);
        json.put("line2", this.line2);
        json.put("city", this.city);
        json.put("province", this.province);
        json.put("postcode", this.postcode);
        return json;
    }
}
