package HandlerAll;

import Model.Customer;
import Model.Subscription;
import Model.SubscriptionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handler untuk menangani permintaan HTTP yang berkaitan dengan entitas "Customer"
public class CustomerHandler implements HttpHandler {

    private final String apiKey;

    public CustomerHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        String path = exchange.getRequestURI().getPath();

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (!apiKey.equals(authHeader)) {
            sendResponse(exchange, HttpURLConnection.HTTP_FORBIDDEN, "{\"error\":\"Forbidden\"}");
            return;
        }

        try {
            switch (method) {
                case "GET":
                    if (path.matches("/customers/?")) {
                        getAllCustomers(exchange);
                    } else if (path.matches("/customers/\\d+/?")) {
                        getCustomerById(exchange);
                    } else if (path.matches("/customers/\\d+/cards/?")) {
                        getCustomerCards(exchange);
                    } else if (path.matches("/customers/\\d+/subscriptions/?")) {
                        getCustomerSubscriptions(exchange);
                    } else if (path.matches("/customers/\\d+/subscriptions\\?subscriptions_status=(active|cancelled|non-renewing)")) {
                        getCustomerSubscriptionsByStatus(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "POST":
                    if (path.matches("/customers/?")) {
                        addCustomer(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "PUT":
                    if (path.matches("/customers/\\d+/?")) {
                        updateCustomer(exchange);
                    } else if (path.matches("/customers/\\d+/shipping_addresses/\\d+/?")) {
                        updateShippingAddress(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "DELETE":
                    if (path.matches("/customers/\\d+/?")) {
                        try {
                            // Extract customerId from the URI
                            String[] parts = exchange.getRequestURI().getPath().split("/");
                            int customerId = Integer.parseInt(parts[2]);

                            deleteCustomer(customerId); // Adjust this line according to your method signature
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "Invalid request");
                        }
                    } else if (path.matches("/customers/\\d+/cards/\\d+/?")) {
                        deleteCustomerCard(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                default:
                    sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Internal Server Error\"}");
        }
    }

    private void getAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id"));
                    customer.setEmail(rs.getString("email"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPhoneNumber(rs.getInt("phone_number"));
                    customers.add(customer);
                }
            }
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(customers).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customers\"}");
        }
    }

    private void getCustomerById(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setEmail(rs.getString("email"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setPhoneNumber(rs.getInt("phone_number"));

                        sendResponse(exchange, HttpURLConnection.HTTP_OK, customer.toJSON().toString());
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer\"}");
        }
    }

    private void getCustomerCards(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customer = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> cards = getCustomerCardsFromDatabase(customer, conn);

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(cards).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer cards\"}");
        }
    }

    private List<String> getCustomerCardsFromDatabase(int customer, Connection conn) throws SQLException {
        List<String> cards = new ArrayList<>();
        String sql = "SELECT masked_number FROM cards WHERE customer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(rs.getString("masked_number"));
                }
            }
        }
        return cards;
    }

    private void getCustomerSubscriptions(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customer = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Subscription> subscriptions = getAllCustomerSubscriptionsFromDatabase(customer, conn);

            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(subscriptions);

            sendResponse(exchange, HttpURLConnection.HTTP_OK, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer subscriptions\"}");
        }
    }

    private List<Subscription> getAllCustomerSubscriptionsFromDatabase(int customer, Connection conn) throws SQLException {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE customers = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subscription subscription = new Subscription();
                    subscription.setId(rs.getInt("id"));
                    subscription.setCustomerId(rs.getInt("customers"));
                    subscription.setBillingPeriod(rs.getInt("billing_period"));
                    subscription.setBillingPeriodUnit(rs.getString("billing_period_unit"));
                    subscription.setTotalDue(rs.getInt("total_due"));
                    subscription.setActivatedAt(rs.getString("activated_at"));
                    subscription.setCurrentTermStart(rs.getString("current_term_start"));
                    subscription.setCurrentTermEnd(rs.getString("current_term_end"));
                    subscription.setStatus(rs.getString("status"));

                    subscriptions.add(subscription);
                }
            }
        }
        return subscriptions;
    }

    private void getCustomerSubscriptionsByStatus(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan status subscriptions dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customer = Integer.parseInt(parts[2]);
        String subscriptionsStatus = exchange.getRequestURI().getQuery().split("=")[1];

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> subscriptions = getCustomerSubscriptionsByStatus(customer, subscriptionsStatus, conn);

            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(subscriptions).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer subscriptions by status\"}");
        }
    }

    private void addCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan payload (data JSON) dari body permintaan
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (accumulator, actual) -> accumulator + actual);

        try {
            // Parsing JSON payload ke objek Customer
            JSONObject jsonObject = new JSONObject(requestBody);
            String email = jsonObject.getString("email");
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String phoneNumber = jsonObject.getString("phone_number");

            // Validasi data pelanggan (contoh sederhana)
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                // Jika data tidak lengkap, kirim respons HTTP 400 (Bad Request)
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing required fields\"}");
                return;
            }

            // Simpan data pelanggan ke database
            int customer = saveCustomer(email, firstName, lastName, phoneNumber);

            // Jika berhasil disimpan, kirim respons HTTP 201 (Created) dengan ID pelanggan baru
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", customer);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not add customer\"}");
        }
    }

    // Metode untuk memperbarui data pelanggan berdasarkan ID
    private void updateCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        // Mendapatkan payload (data JSON) dari body permintaan
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (accumulator, actual) -> accumulator + actual);

        try {
            // Parsing JSON payload ke objek Customer
            JSONObject jsonObject = new JSONObject(requestBody);
            String email = jsonObject.getString("email");
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String phoneNumber = jsonObject.getString("phone_number");

            // Validasi data pelanggan (contoh sederhana)
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                // Jika data tidak lengkap, kirim respons HTTP 400 (Bad Request)
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing required fields\"}");
                return;
            }

            // Perbarui data pelanggan di database
            updateCustomer(customerId, email, firstName, lastName, phoneNumber);

            // Mengirimkan respons dengan status 204 (No Content) untuk mengindikasikan berhasil diperbarui
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update customer\"}");
        }
    }

    // Metode untuk memperbarui alamat pengiriman pelanggan berdasarkan ID
    private void updateShippingAddress(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan ID alamat dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);
        int addressId = Integer.parseInt(parts[4]);

        try {
            // Mendapatkan payload (data JSON) dari body permintaan
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            // Parsing JSON payload ke objek alamat pengiriman
            JSONObject jsonObject = new JSONObject(requestBody.toString());

            // Mendapatkan nilai atribut dari JSON
            String title = jsonObject.optString("title", null);
            String line1 = jsonObject.optString("line1", null);
            String line2 = jsonObject.optString("line2", null);
            String city = jsonObject.optString("city", null);
            String province = jsonObject.optString("province", null);
            String postcode = jsonObject.optString("postcode", null);

            // Perbarui data alamat pengiriman di database
            updateShippingAddressInDatabase(addressId, title, line1, line2, city, province, postcode);

            // Mengirimkan respons dengan status 200 (OK) dan pesan sukses
            String response = "{\"message\":\"Berhasil diupdate\"}";
            sendResponse(exchange, HttpURLConnection.HTTP_OK, response);
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update shipping address\"}");
        }
    }

    // Metode untuk melakukan update alamat pengiriman di database
    private void updateShippingAddressInDatabase(int addressId, String title, String line1, String line2,
                                                 String city, String province, String postcode) throws SQLException {
        String sql = "UPDATE shipping_addresses SET title = ?, line1 = ?, line2 = ?, city = ?, province = ?, postcode = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, line1);
            stmt.setString(3, line2);
            stmt.setString(4, city);
            stmt.setString(5, province);
            stmt.setString(6, postcode);
            stmt.setInt(7, addressId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating shipping address failed, no rows affected.");
            }
        }
    }


    // Method to handle DELETE request for deleting customer card
    public void deleteCustomerCard(HttpExchange exchange) throws IOException {
        try {
            // Extract customerId and cardId from the URI
            String[] parts = exchange.getRequestURI().getPath().split("/");
            int customerId = Integer.parseInt(parts[2]);
            int cardId = Integer.parseInt(parts[4]);

            // Validate and delete card
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check if card is not primary before deleting
                if (!isCardPrimary(cardId, conn)) {
                    deleteCardFromDatabase(cardId, conn);
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "Card deleted successfully");
                } else {
                    sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "Cannot delete primary card");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "Failed to delete card");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "Invalid request");
        }
    }

    // Method to check if the card is primary
    private boolean isCardPrimary(int cardId, Connection conn) throws SQLException {
        String sql = "SELECT is_primary FROM cards WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_primary");
                }
            }
        }
        return false; // Return false if card not found or is_primary not explicitly set
    }

    // Method to delete the card from database
    private void deleteCardFromDatabase(int cardId, Connection conn) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            stmt.executeUpdate();
        }
    }


    // Metode untuk mengirimkan respons HTTP dengan status dan data tertentu
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Method to save customer to database and return the generated customer ID
    private int saveCustomer(String email, String firstName, String lastName, String phoneNumber) throws SQLException {
        String sql = "INSERT INTO customers (email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated customer ID
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    // Method to update customer information in the database
    private void updateCustomer(int customerId, String email, String firstName, String lastName, String phoneNumber) throws SQLException {
        String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, customerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
        }
    }

    private void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting customer failed, no rows affected.");
            }
        }
    }

    private List<String> getCustomerSubscriptions(int customerId, Connection conn) throws SQLException {
        List<String> subscriptions = new ArrayList<>();
        String sql = "SELECT subscription_name FROM customer_subscriptions WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(rs.getString("subscription_name"));
                }
            }
        }
        return subscriptions;
    }

    private List<String> getCustomerSubscriptionsByStatus(int customerId, String status, Connection conn) throws SQLException {
        List<String> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM customer_subscriptions WHERE customer_id = ? AND subscription_status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(rs.getString("subscription_name"));
                }
            }
        }
        return subscriptions;
    }
}
