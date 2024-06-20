package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HandlerPostSubscription implements HttpHandler {
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

                if (!jsonRequest.has("customer") || !jsonRequest.has("billing_period") || !jsonRequest.has("billing_period_unit") ||
                        !jsonRequest.has("total_due") || !jsonRequest.has("activated_at") || !jsonRequest.has("current_term_start") ||
                        !jsonRequest.has("current_term_end") || !jsonRequest.has("status") || !jsonRequest.has("shipping_address") ||
                        !jsonRequest.has("card") || !jsonRequest.has("items")) {
                    sendResponse(exchange, 400, "BAD REQUEST: Missing required fields");
                    return;
                }

                // Menghubungkan ke database SQLite
                try (Connection conn = DatabaseConnection.getConnection()) {
                    addSubscription(conn, jsonRequest);
                    sendResponse(exchange, 201, "Subscription added successfully");
                }
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

    private void addSubscription(Connection conn, JSONObject json) throws SQLException {
        String sqlSubscription = "INSERT INTO subscriptions (customer_id, billing_period, billing_period_unit, total_due, activated_at, current_term_start, current_term_end, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmtSubscription = conn.prepareStatement(sqlSubscription, Statement.RETURN_GENERATED_KEYS)) {
            pstmtSubscription.setInt(1, json.getInt("customer"));
            pstmtSubscription.setInt(2, json.getInt("billing_period"));
            pstmtSubscription.setString(3, json.getString("billing_period_unit"));
            pstmtSubscription.setInt(4, json.getInt("total_due"));
            pstmtSubscription.setDate(5, Date.valueOf(json.getString("activated_at")));
            pstmtSubscription.setDate(6, Date.valueOf(json.getString("current_term_start")));
            pstmtSubscription.setDate(7, Date.valueOf(json.getString("current_term_end")));
            pstmtSubscription.setString(8, json.getString("status"));
            pstmtSubscription.executeUpdate();

            // Mendapatkan ID subscription yang baru dibuat
            ResultSet generatedKeys = pstmtSubscription.getGeneratedKeys();
            if (generatedKeys.next()) {
                int subscriptionId = generatedKeys.getInt(1);

                String sqlShippingAddress = "INSERT INTO shipping_addresses (subscription_id, address_line1, address_line2, city, state, zip_code, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmtShipping = conn.prepareStatement(sqlShippingAddress)) {
                    JSONObject shippingAddress = json.getJSONObject("shipping_address");
                    pstmtShipping.setInt(1, subscriptionId);
                    pstmtShipping.setString(2, shippingAddress.getString("address_line1"));
                    pstmtShipping.setString(3, shippingAddress.getString("address_line2"));
                    pstmtShipping.setString(4, shippingAddress.getString("city"));
                    pstmtShipping.setString(5, shippingAddress.getString("state"));
                    pstmtShipping.setString(6, shippingAddress.getString("zip_code"));
                    pstmtShipping.setString(7, shippingAddress.getString("country"));
                    pstmtShipping.executeUpdate();
                }

                String sqlCard = "INSERT INTO cards (subscription_id, card_number, expiry_date, card_type) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmtCard = conn.prepareStatement(sqlCard)) {
                    JSONObject card = json.getJSONObject("card");
                    pstmtCard.setInt(1, subscriptionId);
                    pstmtCard.setString(2, card.getString("card_number"));
                    pstmtCard.setString(3, card.getString("expiry_date"));
                    pstmtCard.setString(4, card.getString("card_type"));
                    pstmtCard.executeUpdate();
                }

                String sqlItem = "INSERT INTO items (subscription_id, item_name, item_price, quantity) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                    JSONArray items = json.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        pstmtItem.setInt(1, subscriptionId);
                        pstmtItem.setString(2, item.getString("item_name"));
                        pstmtItem.setInt(3, item.getInt("item_price"));
                        pstmtItem.setInt(4, item.getInt("quantity"));
                        pstmtItem.addBatch();
                    }
                    pstmtItem.executeBatch();
                }
            }
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

