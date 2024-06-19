package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HandlerDeleteItems implements HttpHandler {
    private final HandlerPutCustomer.DatabaseConnection databaseConnection;

    public HandlerDeleteItems(HandlerPutCustomer.DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("DELETE".equals(exchange.getRequestMethod())) {
            try {
                String response = handleDeleteRequest(exchange);
                sendResponse(exchange, 200, response);
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

    private String handleDeleteRequest(HttpExchange exchange) throws SQLException, IOException {
        // Mendapatkan path dari permintaan
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length != 3 || !pathSegments[1].equalsIgnoreCase("items")) {
            throw new IllegalArgumentException("Invalid path");
        }

        int itemId = Integer.parseInt(pathSegments[2]);

        // Menghubungkan ke database SQLite
        try (Connection conn = databaseConnection.getConnection()) {
            String sql = "UPDATE items SET is_active = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBoolean(1, false);
                pstmt.setInt(2, itemId);
                pstmt.executeUpdate();
            }
        }

        return "Item successfully deactivated";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
