package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

public abstract class HandlerGetCustomer implements HttpHandler {
    private DatabaseConnection databaseConnection;

    public HandlerGetCustomer() {
    }

    private String handleGetCustomer(HttpExchange exchange) throws SQLException {
        JSONArray data = new JSONArray();
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");
        String query;
        Connection conn = databaseConnection.getConnection();
        Statement statement = conn.createStatement();

        if (pathSegments.length == 2) {
            if ("customers".equalsIgnoreCase(pathSegments[1])) {
                query = "SELECT * FROM customers";
            } else {
                return invalidPathResponse();
            }
        } else if (pathSegments.length == 3) {
            if ("customers".equalsIgnoreCase(pathSegments[1])) {
                query = "SELECT * FROM customers WHERE id = " + pathSegments[2];
            } else {
                return invalidPathResponse();
            }
        } else if (pathSegments.length == 4) {
            if ("customers".equalsIgnoreCase(pathSegments[1])) {
                if ("cards".equalsIgnoreCase(pathSegments[3])) {
                    query = "SELECT * FROM cards WHERE customer_id = " + pathSegments[2];
                } else if ("subscriptions".equalsIgnoreCase(pathSegments[3])) {
                    query = "SELECT * FROM subscriptions WHERE customer_id = " + pathSegments[2];
                } else {
                    return invalidPathResponse();
                }
            } else {
                return invalidPathResponse();
            }
        } else if (pathSegments.length == 5) {
            if ("customers".equalsIgnoreCase(pathSegments[1]) && "subscriptions".equalsIgnoreCase(pathSegments[3])) {
                query = "SELECT * FROM subscriptions WHERE customer_id = " + pathSegments[2] + " AND status = '" + pathSegments[4] + "'";
            } else {
                return invalidPathResponse();
            }
        } else {
            return invalidPathResponse();
        }

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            if ("customers".equalsIgnoreCase(pathSegments[1])) {
                Customer customer = new Customer();
                customer.setId(resultSet.getInt("id"));
                customer.setFirstName(resultSet.getString("first_name"));
                customer.setLastName(resultSet.getString("last_name"));
                customer.setEmail(resultSet.getString("email"));
                customer.setPhoneNumber(resultSet.getString("phone_number"));
                data.put(customer.toJsonObject());

                if (pathSegments.length == 3) {
                    query = "SELECT * FROM addresses WHERE customer_id = " + pathSegments[2];
                    ResultSet addressResultSet = statement.executeQuery(query);
                    while (addressResultSet.next()) {
                        ShippingAddresses addresses = new ShippingAddresses();
                        addresses.setId(addressResultSet.getInt("id"));
                        addresses.setCustomerId(addressResultSet.getInt("customer_id"));
                        addresses.setTitle(addressResultSet.getString("title"));
                        addresses.setLine1(addressResultSet.getString("line1"));
                        addresses.setLine2(addressResultSet.getString("line2"));
                        addresses.setCity(addressResultSet.getString("city"));
                        addresses.setProvince(addressResultSet.getString("province"));
                        addresses.setPostcode(addressResultSet.getString("postcode"));
                        data.put(addresses.toJsonObject());
                    }
                }
            } else if ("cards".equalsIgnoreCase(pathSegments[1])) {
                Cards cards = new Cards();
                cards.setId(resultSet.getInt("id"));
                cards.setCustomerId(resultSet.getInt("customer_id"));
                cards.setCardNumber(resultSet.getString("card_number"));
                cards.setExpiryDate(resultSet.getString("expiry_date"));
                cards.setCardType(resultSet.getString("card_type"));
                data.put(cards.toJsonObject());
            } else if ("subscriptions".equalsIgnoreCase(pathSegments[1])) {
                Subscriptions subscriptions = new Subscriptions();
                subscriptions.setId(resultSet.getInt("id"));
                subscriptions.setCustomerId(resultSet.getInt("customer_id"));
                subscriptions.setStatus(resultSet.getString("status"));
                subscriptions.setStartDate(resultSet.getDate("start_date"));
                subscriptions.setEndDate(resultSet.getDate("end_date"));
                data.put(subscriptions.toJsonObject());
            }
        }

        resultSet.close();
        statement.close();
        conn.close();

        return data.toString(2);
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

    static class DatabaseConnection {
        public Connection getConnection() {
            return null;
        }
    }
}
