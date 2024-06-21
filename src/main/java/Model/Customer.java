package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

public class Customer {

    private int id;
    private String email;
    private String first_name;
    private String last_name;
    private String phone_number;
    private List<String> shipping_addresses;

    // Constructors, getters, and setters
    public Customer() {
    }

    public Customer(int id, String email, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.first_name = firstName;
        this.last_name = lastName;
        this.phone_number = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String firstName) {
        this.first_name = firstName;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String lastName) {
        this.last_name = lastName;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public List<String> getAddresses() {
        return shipping_addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.shipping_addresses = addresses;
    }

    // Method to convert Customer object to JSON object
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("email", email);
        json.put("firstname", first_name);
        json.put("lastname", last_name);
        json.put("phonenumber", phone_number);

        // Adding addresses if present
        if (shipping_addresses != null && !shipping_addresses.isEmpty()) {
            JSONArray addressesArray = new JSONArray(shipping_addresses);
            json.put("addresses", addressesArray);
        }

        return json;
    }
}
