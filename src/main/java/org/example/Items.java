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

    private static class DatabaseConnection {
        public static Connection getConnection() {
            return null;
        }
    }
}

