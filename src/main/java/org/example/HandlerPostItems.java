package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public abstract class HandlerPostItems implements HttpHandler {
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

    private void sendResponse(HttpExchange exchange, int i, String methodNotAllowed) {
    }

    private String handlePostItems(HttpExchange exchange) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JSONObject jsonRequest = new JSONObject(requestBody.toString());

        if (!jsonRequest.has("name") || !jsonRequest.has("price") || !jsonRequest.has("type")) {
            throw new IllegalArgumentException("BAD REQUEST: Missing required fields");
        }

        String name = jsonRequest.getString("name");
        int price = jsonRequest.getInt("price");
        String type = jsonRequest.getString("type");

        if (!type.equals("plan") && !type.equals("addon")) {
            throw new IllegalArgumentException("BAD REQUEST: Invalid type");
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
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
    }



