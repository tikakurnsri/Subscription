package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;

public abstract class HandlerPostItems implements HttpHandler {
    private HandlerGetCustomer.DatabaseConnection databaseConnection;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String response;
            try {
                response = handlePostItems(exchange);
                sendResponse(exchange, 201, response);
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Internal server error");
            } catch (IllegalArgumentException e) {
                sendResponse(exchange, 400, e.getMessage());
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private String handlePostItems(HttpExchange exchange) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JSONObject jsonRequest = new JSONObject(requestBody.toString());

        // Memastikan semua data yang diperlukan ada
        if (!jsonRequest.has("name") || !jsonRequest.has("price") || !jsonRequest.has("type")) {
            throw new IllegalArgumentException("BAD REQUEST: Missing required fields");
        }

        String name = jsonRequest.getString("name");
        int price = jsonRequest.getInt("price");
        String type = jsonRequest.getString("type");

        if (!type.equals("plan") && !type.equals("addon")) {
            throw new IllegalArgumentException("BAD REQUEST: Invalid type");
        }

        try (Connection conn = databaseConnection.getConnection()) {
            String sql = "INSERT INTO items (name, price, type, is_active) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, price);
                pstmt.setString(3, type);
                pstmt.setBoolean(4, true);
                pstmt.executeUpdate();
            }
        }

        return "Item successfully created";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
