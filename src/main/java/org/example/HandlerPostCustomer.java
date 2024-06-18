package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HandlerPostCustomer implements HttpHandler {
    private static Connection conn;
    private DatabaseConnection DatabaseConnection;

    @Override
    public void handle(HttpExchange exchange) {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                // Membaca data dari body permintaan
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                JSONObject jsonRequest = new JSONObject(requestBody.toString());

                String path = exchange.getRequestURI().getPath();
                String[] pathSegments = path.split("/");

                // Menghubungkan ke database SQLite
                conn = DatabaseConnection.getConnection();

                if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                    addCustomer(jsonRequest);
                } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("customers")) {
                    String subResource = pathSegments[3];
                    if (subResource.equalsIgnoreCase("cards")) {
                        addCard(jsonRequest, Integer.parseInt(pathSegments[2]));
                    } else if (subResource.equalsIgnoreCase("subscriptions")) {
                        addSubscription(jsonRequest, Integer.parseInt(pathSegments[2]));
                    }
                } else {
                    sendResponse(exchange, 400, "Invalid path");
                    return;
                }

                sendResponse(exchange, 200, "Data successfully inserted");
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    sendResponse(exchange, 500, "Internal server error");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            try {
                sendResponse(exchange, 405, "Method Not Allowed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addCustomer(JSONObject json) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, json.getString("first_name"));
            pstmt.setString(2, json.getString("last_name"));
            pstmt.setString(3, json.getString("email"));
            pstmt.setString(4, json.getString("phone_number"));
            pstmt.executeUpdate();
        }
    }

    private void addCard(JSONObject json, int customerId) throws SQLException {
        String sql = "INSERT INTO cards (customer_id, card_number, expiry_date, card_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, json.getString("card_number"));
            pstmt.setString(3, json.getString("expiry_date"));
            pstmt.setString(4, json.getString("card_type"));
            pstmt.executeUpdate();
        }
    }

    private void addSubscription(JSONObject json, int customerId) throws SQLException {
        String sql = "INSERT INTO subscriptions (customer_id, status, start_date, end_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, json.getString("status"));
            pstmt.setDate(3, Date.valueOf(json.getString("start_date")));
            pstmt.setDate(4, Date.valueOf(json.getString("end_date")));
            pstmt.executeUpdate();
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws Exception {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static class DatabaseConnection {
        public Connection getConnection() {
            return null;
        }
    }
}
