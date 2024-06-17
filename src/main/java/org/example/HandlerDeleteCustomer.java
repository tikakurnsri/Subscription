import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HandlerDeleteCustomer implements HttpHandler {
    private static Connection conn;

    @Override
    public void handle(HttpExchange exchange) {
        if ("DELETE".equals(exchange.getRequestMethod())) {
            try {
                // Mendapatkan path dari permintaan
                String path = exchange.getRequestURI().getPath();
                String[] pathSegments = path.split("/");

                // Memeriksa path untuk validitas
                if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                    String id = pathSegments[2];

                    // Menghubungkan ke database SQLite
                    conn = DatabaseConnection.getConnection();

                    // Menghapus pelanggan berdasarkan ID
                    deleteCustomer(id);

                    sendResponse(exchange, 200, "Customer deleted successfully");
                } else {
                    sendResponse(exchange, 400, "Invalid path");
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

    private void deleteCustomer(String id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.executeUpdate();
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws Exception {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
