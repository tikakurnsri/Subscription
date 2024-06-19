package org.example;

import javax.naming.spi.DirStateFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                String url = "jdbc:sqlite:subcription.db";
                conn = DriverManager.getConnection(url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return conn;
    }

    public Result selectFromTable(String tableName, String condition) {
        return null;
    }

    public Result insertToTable(String tableName, String string, String string1) {
        return null;
    }

    public Result updateTable(String tableName, int id, String string) {
        return null;
    }

    public Result deleteTable(String tableName, int id) {
        return null;
    }
}
