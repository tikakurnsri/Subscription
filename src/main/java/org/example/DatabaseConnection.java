package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:subcription.db";
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL);
    }

    public Result selectFromTable(String tableName, String condition) {
    Connection conn = null;
    Statement stmt = null;
    try {
        conn = DriverManager.getConnection(DB_URL);
        stmt = conn.createStatement();
        String sql = "SELECT * FROM " + tableName + " WHERE " + condition;
        ResultSet rs = stmt.executeQuery(sql);

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
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    public Result insertToTable(String tableName, String fieldKeys, String fieldValues) {
    Connection conn = null;
    Statement stmt = null;
    try {
        conn = DriverManager.getConnection(DB_URL);
        stmt = conn.createStatement();
        String sql = "INSERT INTO " + tableName + " (" + fieldKeys + ") VALUES (" + fieldValues + ")";
        int rowsAffected = stmt.executeUpdate(sql);

        if (rowsAffected > 0) {
            return new Result(200, true, "Insert successful", null);
        } else {
            return new Result(400, false, "Insert failed", null);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return new Result(500, false, "Internal server error", null);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    public Result updateTable(String tableName, int id, String updates) {
    Connection conn = null;
    Statement stmt = null;
    try {
        conn = DriverManager.getConnection(DB_URL);
        stmt = conn.createStatement();
        String sql = "UPDATE " + tableName + " SET " + updates + " WHERE id=" + id;
        int rowsAffected = stmt.executeUpdate(sql);

        if (rowsAffected > 0) {
            return new Result(200, true, "Update successful", null);
        } else {
            return new Result(400, false, "Update failed", null);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return new Result(500, false, "Internal server error", null);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    public Result deleteTable(String tableName, int id) {
    Connection conn = null;
    Statement stmt = null;
    try {
        conn = DriverManager.getConnection(DB_URL);
        stmt = conn.createStatement();
        String sql = "DELETE FROM " + tableName + " WHERE id=" + id;
        int rowsAffected = stmt.executeUpdate(sql);

        if (rowsAffected > 0) {
            return new Result(200, true, "Delete successful", null);
        } else {
            return new Result(400, false, "Delete failed", null);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return new Result(500, false, "Internal server error", null);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

}
