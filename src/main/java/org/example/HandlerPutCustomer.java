package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HandlerPutCustomer implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            String response;
            String path = exchange.getRequestURI().getPath();
            String[] pathSegments = path.split("/");

            if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                String id = pathSegments[2];
                Customer customer = new Customer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                if (Customer.parseUserJSON(requestBody.toString()) == 0) {
                    Customer.updateCustomer(id);
                    response = "Customer updated successfully";
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 400, "Invalid input");
                }
            } else {
                sendResponse(exchange, 400, "Invalid path");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public interface DatabaseConnection {
        Connection getConnection();
    }
}
