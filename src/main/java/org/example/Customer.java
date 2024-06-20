package org.example;

import org.json.JSONObject;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public static void updateCustomer(String id) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("first_name", firstName);
        jsonObject.put("last_name", lastName);
        jsonObject.put("email", email);
        jsonObject.put("phone_number", phoneNumber);
        return jsonObject;
    }

    public void addCustomer() {
    }

    public static int parseUserJSON(String string) {
        return 0;
    }
}
