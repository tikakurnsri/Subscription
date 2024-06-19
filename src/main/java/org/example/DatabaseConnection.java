package org.example;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:subcription.db";
    private static Connection connection = null;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public Result selectFromTable(String tableName, String condition) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE " + condition);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder jsonData = new StringBuilder("[");
            while (rs.next()) {
                jsonData.append("{")
                        .append("\"id\": ").append(rs.getInt("id")).append(",")
                        .append("\"name\": \"").append(rs.getString("name")).append("\",")
                        .append("\"value\": \"").append(rs.getString("value")).append("\"")
                        .append("},");
            }
            if (jsonData.length() > 1) {
                jsonData.deleteCharAt(jsonData.length() - 1); // Remove last comma
            }
            jsonData.append("]");

            return new Result(200, true, "Data retrieved successfully", jsonData.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(500, false, "Internal server error", null);
        }
    }

    public Result insertToTable(String tableName, String fieldKeys, String fieldValues) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO " + tableName + " (" + fieldKeys + ") VALUES (" + fieldValues + ")")) {

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return new Result(200, true, "Insert successful", null);
            } else {
                return new Result(400, false, "Insert failed", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(500, false, "Internal server error", null);
        }
    }

    public Result updateTable(String tableName, int id, String updates) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE " + tableName + " SET " + updates + " WHERE id=?")) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return new Result(200, true, "Update successful", null);
            } else {
                return new Result(400, false, "Update failed", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(500, false, "Internal server error", null);
        }
    }

    public Result deleteTable(String tableName, int id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE id=?")) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return new Result(200, true, "Delete successful", null);
            } else {
                return new Result(400, false, "Delete failed", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result(500, false, "Internal server error", null);
        }
    }
}
