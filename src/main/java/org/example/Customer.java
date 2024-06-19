package org.example;

import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//import static org.example.HandlerGetCustomer.DatabaseConnection;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String type;
    private String idCustomer;

    public Customer(String idCustomer) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.phoneNumber = phoneNumber;
//        this.type = type;
        this.idCustomer = idCustomer;
    }

    public Customer() {

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", id);
        jsonObject.put("first_name", firstName);
        jsonObject.put("last_name", lastName);
        jsonObject.put("email", email);
        jsonObject.put("phone_number", phoneNumber);
        jsonObject.put("tipe", type);
        return jsonObject;
    }

    public int parseUserJSON(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            firstName = obj.getString("first_name");
            lastName = obj.getString("last_name");
            email = obj.getString("email");
            phoneNumber = obj.getString("phone_number");
            type = obj.getString("tipe");
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public void addCustomers() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO users (first_name, last_name, email, phone_number, tipe) VALUES (?,?,?,?,?)";
            assert conn != null;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCustomers(String idUser) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET first_name = \"" + firstName +
                    "\" , last_name = \"" + lastName +
                    "\" , email = \"" + email +
                    "\" , phone_number = \"" + phoneNumber +
                    "\" WHERE customers = " + idCustomer;
            assert conn != null;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class DatabaseConnection {
        public static Connection getConnection() {
            return null;
        }
    }
}

