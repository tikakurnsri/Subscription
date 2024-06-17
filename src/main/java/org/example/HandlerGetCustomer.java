package org.example;

import com.sun.net.httpserver.HttpExchange;
import java.sql.*;
import org.json.JSONArray;

public class HandlerGetCustomer {
    private static String tableName;
    private static String id;
    private static String path;
    private static String query;
    private static String response;
    private static String[] pathSegments;
    private static JSONArray data = new JSONArray();
    private static ResultSet resultSet;
    private static Connection conn;
    private static Statement statement;

    public static String HandlerGetCustomer (HttpExchange exchange) throws SQLException {
        try {
            // Menghapus data pada JSONArray untuk menghindari duplikasi data
            data.clear();

            // Mendapatkan path dari permintaan
            path = exchange.getRequestURI().getPath();

            // Memisahkan path menjadi endpoint dan id
            pathSegments = path.split("/");

            // Mendapatkan query string dari permintaan
            query = exchange.getRequestURI().getQuery();

            // Menghubungkan ke database SQLite
            conn = DatabaseConnection.getConnection();
            statement = conn.createStatement();

            // Memastikan segment pertama adalah nama tabel
            if (pathSegments.length == 2) {
                tableName = pathSegments[1];
                if (tableName.equalsIgnoreCase("customers")) {
                    query = "SELECT * FROM customers";
                }
            } else if (pathSegments.length == 3) {
                tableName = pathSegments[1];
                id = pathSegments[2];
                if (tableName.equalsIgnoreCase("customers")) {
                    query = "SELECT * FROM customers WHERE id = " + id;
                }
            } else if (pathSegments.length == 4) {
                tableName = pathSegments[1];
                id = pathSegments[2];
                String subResource = pathSegments[3];
                if (tableName.equalsIgnoreCase("customers")) {
                    if (subResource.equalsIgnoreCase("cards")) {
                        query = "SELECT * FROM cards WHERE customer_id = " + id;
                    } else if (subResource.equalsIgnoreCase("subscriptions")) {
                        query = "SELECT * FROM subscriptions WHERE customer_id = " + id;
                    }
                }
            } else if (pathSegments.length == 5) {
                tableName = pathSegments[1];
                id = pathSegments[2];
                String subResource = pathSegments[3];
                if (tableName.equalsIgnoreCase("customers") && subResource.equalsIgnoreCase("subscriptions")) {
                    String subStatus = pathSegments[4];
                    query = "SELECT * FROM subscriptions WHERE customer_id = " + id + " AND status = '" + subStatus + "'";
                }
            } else {
                // Jika tidak ada nama tabel, kembalikan respon error
                data.put(Fitur.invalidPath(exchange));
            }

            // Memeriksa apakah tabel valid
            if (!Fitur.isValidTable(tableName)) {
                // Jika tabel tidak valid, kembalikan respon error
                data.put(Fitur.unvaliableTable(tableName));
            }

            resultSet = statement.executeQuery(query);

            // Mengambil hasil query dan menyimpannya dalam JSONArray
            while (resultSet.next()) {
                if (tableName.equals("customers")) {
                    Customers customers = new Customers();
                    customers.setId(resultSet.getInt("id"));
                    customers.setFirstName(resultSet.getString("first_name"));
                    customers.setLastName(resultSet.getString("last_name"));
                    customers.setEmail(resultSet.getString("email"));
                    customers.setPhoneNumber(resultSet.getString("phone_number"));
                    data.put(customers.toJsonObject());

                    if (pathSegments.length == 3) {
                        query = "SELECT * FROM addresses WHERE customer_id = " + id;
                        ResultSet addressResultSet = statement.executeQuery(query);
                        while (addressResultSet.next()) {
                            Addresses addresses = new Addresses();
                            addresses.setId(addressResultSet.getInt("id"));
                            addresses.setCustomerId(addressResultSet.getInt("customer_id"));
                            addresses.setLine1(addressResultSet.getString("line1"));
                            addresses.setLine2(addressResultSet.getString("line2"));
                            addresses.setCity(addressResultSet.getString("city"));
                            addresses.setProvince(addressResultSet.getString("province"));
                            addresses.setPostcode(addressResultSet.getString("postcode"));
                            data.put(addresses.toJsonObject());
                        }
                    }
                } else if (tableName.equals("cards")) {
                    Cards cards = new Cards();
                    cards.setId(resultSet.getInt("id"));
                    cards.setCustomerId(resultSet.getInt("customer_id"));
                    cards.setCardNumber(resultSet.getString("card_number"));
                    cards.setExpiryDate(resultSet.getString("expiry_date"));
                    cards.setCardType(resultSet.getString("card_type"));
                    data.put(cards.toJsonObject());
                } else if (tableName.equals("subscriptions")) {
                    Subscriptions subscriptions = new Subscriptions();
                    subscriptions.setId(resultSet.getInt("id"));
                    subscriptions.setCustomerId(resultSet.getInt("customer_id"));
                    subscriptions.setStatus(resultSet.getString("status"));
                    subscriptions.setStartDate(resultSet.getDate("start_date"));
                    subscriptions.setEndDate(resultSet.getDate("end_date"));
                    data.put(subscriptions.toJsonObject());
                } else {
                    data.put(Fitur.unvaliableTable(tableName));
                }
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mengatur indentasi pada JSON
        response = data.toString(2);
        return response;
    }
}

