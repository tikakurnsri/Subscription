package org.example;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;

public class Items {
    private int id;
    private String Name;
    private int price;
    private String type;
    private boolean is_active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String setType(String type) {this.type = type;
        return type;
    }

    public void getType(String type) {this.type = type;    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("items", id);
        jsonObject.put("name", Name);
        jsonObject.put("type", price);
        jsonObject.put("type", Collections.singleton(type));
        jsonObject.put("is_active", is_active);
        return jsonObject;
    }

    public int parseUserJSON(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            Name = obj.getString("name");
            is_active = obj.getBoolean("is_active");
            price = obj.getInt("price");
            type = obj.getString("tipe");
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public void insertCustomers() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO users (first_name, last_name, email, phone_number, tipe) VALUES (?,?,?,?,?)";
            assert conn != null;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Name);
            pstmt.setInt(2, price);
            pstmt.setString(3, type);
            pstmt.setBoolean(4,is_active);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateCustomers(String idUser) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE users SET Name = \"" + Name +
                    "\" , price = \"" + price +
                    "\" WHERE customers = " + is_active;
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

