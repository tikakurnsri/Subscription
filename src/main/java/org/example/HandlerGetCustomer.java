package org.example;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HandlerGetCustomer {
    public void handleGetCustomer(HttpExchange exchange) throws IOException {
        JSONArray data = new JSONArray();
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");
        String query;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement()) {

            if (pathSegments.length == 2 && "customers".equalsIgnoreCase(pathSegments[1])) {
                query = "SELECT * FROM customers";
            } else if (pathSegments.length == 3 && "customers".equalsIgnoreCase(pathSegments[1])) {
                query = "SELECT * FROM customers WHERE id = " + pathSegments[2];
            } else if (pathSegments.length == 4 && "customers".equalsIgnoreCase(pathSegments[1])) {
                if ("cards".equalsIgnoreCase(pathSegments[3])) {
                    query = "SELECT * FROM cards WHERE customer_id = " + pathSegments[2];
                } else if ("subscriptions".equalsIgnoreCase(pathSegments[3])) {
                    query = "SELECT * FROM subcription WHERE customer_id = " + pathSegments[2];
                } else {
                    sendResponse(exchange, 400, invalidPathResponse());
                    return;
                }
            } else if (pathSegments.length == 5 && "customers".equalsIgnoreCase(pathSegments[1]) && "subcription".equalsIgnoreCase(pathSegments[3])) {
                query = "SELECT * FROM subcription WHERE customer_id = " + pathSegments[2] + " AND status = '" + pathSegments[4] + "'";
            } else {
                sendResponse(exchange, 400, invalidPathResponse());
                return;
            }

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if ("customers".equalsIgnoreCase(pathSegments[1])) {
                    JSONObject customer = new JSONObject();
                    customer.put("id", resultSet.getInt("id"));
                    customer.put("firstName", resultSet.getString("first_name"));
                    customer.put("lastName", resultSet.getString("last_name"));
                    customer.put("email", resultSet.getString("email"));
                    customer.put("phoneNumber", resultSet.getString("phone_number"));
                    data.put(customer);

                    if (pathSegments.length == 3) {
                        String addressQuery = "SELECT * FROM shipping_addresses WHERE customer_id = " + pathSegments[2];
                        try (ResultSet addressResultSet = statement.executeQuery(addressQuery)) {
                            while (addressResultSet.next()) {
                                JSONObject address = new JSONObject();
                                address.put("id", addressResultSet.getInt("id"));
                                address.put("customerId", addressResultSet.getInt("customer_id"));
                                address.put("title", addressResultSet.getString("title"));
                                address.put("line1", addressResultSet.getString("line1"));
                                address.put("line2", addressResultSet.getString("line2"));
                                address.put("city", addressResultSet.getString("city"));
                                address.put("province", addressResultSet.getString("province"));
                                address.put("postcode", addressResultSet.getString("postcode"));
                                data.put(address);
                            }
                        }
                    }
                } else if ("cards".equalsIgnoreCase(pathSegments[1])) {
                    JSONObject card = new JSONObject();
                    card.put("id", resultSet.getInt("id"));
                    card.put("customerId", resultSet.getInt("customer_id"));
                    card.put("cardNumber", resultSet.getString("card_number"));
                    card.put("expiryDate", resultSet.getString("expiry_date"));
                    card.put("cardType", resultSet.getString("card_type"));
                    data.put(card);
                } else if ("subcription".equalsIgnoreCase(pathSegments[1])) {
                    JSONObject subscription = new JSONObject();
                    subscription.put("id", resultSet.getInt("id"));
                    subscription.put("customerId", resultSet.getInt("customer_id"));
                    subscription.put("status", resultSet.getString("status"));
                    subscription.put("startDate", resultSet.getDate("start_date"));
                    subscription.put("endDate", resultSet.getDate("end_date"));
                    data.put(subscription);
                }
            }

            resultSet.close();
            sendResponse(exchange, 200, data.toString(2));

        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Database error\"}");
        }
    }

    private String invalidPathResponse() {
        return "{\"error\":\"Invalid path\"}";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
