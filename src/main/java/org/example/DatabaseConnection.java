package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String url = "jdbc:sqlite:C:/Users/kurni/Documents/Subscription/subcription.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    // Metode untuk inisialisasi database
    public static void init() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQLite database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load SQLite JDBC driver");
        }
    }
}