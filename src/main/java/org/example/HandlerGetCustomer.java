package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HandlerGetCustomer implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String response;
            try {
                String path = exchange.getRequestURI().getPath();
                String[] pathSegments = path.split("/");

                if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                    String id = pathSegments[2];
                    Customer customer = getCustomerById(id);
                    if (customer != null) {
                        Result result = new Result(200, true, "Customer found", customer.toJsonObject().toString());
                        sendResponse(exchange, 200, result.toJson());
                    } else {
                        Result result = new Result(404, false, "Customer not found", null);
                        sendResponse(exchange, 404, result.toJson());
                    }
                } else {
                    Result result = new Result(400, false, "Invalid path", null);
                    sendResponse(exchange, 400, result.toJson());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Result result = new Result(500, false, "Internal server error", null);
                sendResponse(exchange, 500, result.toJson());
            }
            }
                else {
                    Result result = new Result(405, false, "Method Not Allowed", null);
                    sendResponse(exchange, 405, result.toJson());
                }
    }

    private Customer getCustomerById(String id) throws SQLException {
        Customer customer = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(id));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setEmail(rs.getString("email"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setPhoneNumber(rs.getString("phone_number"));
                    }
                }
            }
        }
        return customer;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
