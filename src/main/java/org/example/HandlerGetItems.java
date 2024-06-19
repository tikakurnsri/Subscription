package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HandlerGetItems implements HttpHandler {
    private final DatabaseConnection databaseConnection;

    public HandlerGetItems(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            try {
                String response = handleGetRequest();
                sendResponse(exchange, 200, response);
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private String handleGetRequest() throws SQLException {
        StringBuilder response = new StringBuilder();
        response.append("[");

        try (Connection conn = databaseConnection.getConnection()) {
            String sql = "SELECT * FROM items WHERE is_active = 1";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    if (response.length() > 1) {
                        response.append(",");
                    }
                    response.append("{");
                    response.append("\"id\":").append(rs.getInt("id")).append(",");
                    response.append("\"name\":").append("\"").append(rs.getString("name")).append("\"");
                    response.append("}");
                }
            }
        }

        response.append("]");
        return response.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
