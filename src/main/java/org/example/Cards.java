package org.example;

import org.json.JSONObject;

public class Cards {
    private int id;
    private int customerId;
    private String cardType;
    private String maskedNumber;
    private int expiryMonth;
    private int expiryYear;
    private String status;
    private boolean isPrimary;

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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    // Mengubah objek menjadi JSONObject
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("customer", this.customerId);
        json.put("card_type", this.cardType);
        json.put("masked_number", this.maskedNumber);
        json.put("expiry_month", this.expiryMonth);
        json.put("expiry_year", this.expiryYear);
        json.put("status", this.status);
        json.put("is_primary", this.isPrimary);
        return json;
    }

    public void setCardNumber(String cardNumber) {
    }

    public void setExpiryDate(String expiryDate) {

    }
}
