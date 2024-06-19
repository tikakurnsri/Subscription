package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;

public abstract class HandlerPutCustomer implements HttpHandler {
    private final DatabaseConnection databaseConnection;

    public HandlerPutCustomer(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void handlePutCustomer(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            String response;
            try {
                response = handlePutRequest(exchange);
                sendResponse(exchange, 200, response);
            } catch (SQLException e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error");
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, 400, e.getMessage());
            } catch (NotFoundException e) {
                sendResponse(exchange, 404, e.getMessage());
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private String handlePutRequest(HttpExchange exchange) throws IOException, SQLException, NotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JSONObject jsonRequest = new JSONObject(requestBody.toString());

        // Memastikan semua data yang diperlukan ada
        if (!jsonRequest.has("title") || !jsonRequest.has("line1") || !jsonRequest.has("city")
                || !jsonRequest.has("province") || !jsonRequest.has("postcode")) {
            throw new IllegalArgumentException("BAD REQUEST: Missing required fields");
        }

        // Mendapatkan path dari permintaan
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length != 5 || !pathSegments[1].equalsIgnoreCase("customers")
                || !pathSegments[3].equalsIgnoreCase("shipping_addresses")) {
            throw new IllegalArgumentException("Invalid path");
        }

        int customerId = Integer.parseInt(pathSegments[2]);
        int addressId = Integer.parseInt(pathSegments[4]);

        // Menghubungkan ke database SQLite
        try (Connection conn = databaseConnection.getConnection()) {
            // Memeriksa apakah entitas ada
            if (entityExists(conn, "customers", customerId) || entityExists(conn, "shipping_addresses", addressId)) {
                throw new NotFoundException("Customer or shipping address not found");
            }

            // Memperbarui data
            updateShippingAddress(conn, addressId, jsonRequest);

            return "Shipping address successfully updated";
        }
    }

    private boolean entityExists(Connection conn, String tableName, int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) <= 0;
                } else {
                    return true;
                }
            }
        }
    }

    private void updateShippingAddress(Connection conn, int addressId, JSONObject json) throws SQLException {
        String sql = "UPDATE shipping_addresses SET title = ?, line1 = ?, line2 = ?, city = ?, province = ?, postcode = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, json.getString("title"));
            pstmt.setString(2, json.getString("line1"));
            pstmt.setString(3, json.optString("line2", ""));
            pstmt.setString(4, json.getString("city"));
            pstmt.setString(5, json.getString("province"));
            pstmt.setString(6, json.getString("postcode"));
            pstmt.setInt(7, addressId);
            pstmt.executeUpdate();
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class DatabaseConnection {
        private final String url;

        public DatabaseConnection(String url) {
            this.url = url;
        }

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(url);
        }
    }
}
