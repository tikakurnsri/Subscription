package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        int port = 9034;
        new Server(port).startServer();
    }

    private void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        server.createContext("/customers", new CustomerHandler());
        server.setExecutor(null);
        server.start();
        System.out.printf("Server started on port %d...\n", port);
    }

    private static class CustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathSegments = path.split("/");

            Response response = new Response(exchange);

            try {
                switch (method) {
                    case "GET":
                        handleGetCustomer(exchange, pathSegments, response);
                        break;
                    case "POST":
                        handlePost(exchange, pathSegments, response);
                        break;
                    case "PUT":
                        handlePut(exchange, pathSegments, response);
                        break;
                    case "DELETE":
                        handleDelete(exchange, pathSegments, response);
                        break;
                    default:
                        response.send(405, "{\"status\": 405, \"message\": \"Method Not Allowed\"}");
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.send(500, "{\"status\": 500, \"message\": \"Internal Server Error\"}");
            }
        }

        private void handleGetCustomer(HttpExchange exchange, String[] pathSegments, Response response) throws IOException, SQLException {
            if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                response.handleGet("customers", 0, null);
            } else if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                int customerId = Integer.parseInt(pathSegments[2]);
                response.handleGet("customers", customerId, null);
            } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("customers")) {
                int customerId = Integer.parseInt(pathSegments[2]);
                String detail = pathSegments[3];
                response.handleGet("customers", customerId, detail);
            } else {
                response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
            }
        }

        private void handlePost(HttpExchange exchange, String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                handlePostCustomer(exchange, response);
            } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("customers")) {
                String subResource = pathSegments[3];
                handlePostSubResource(exchange, subResource, response);
            } else {
                response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
            }
        }

        private void handlePostCustomer(HttpExchange exchange, Response response) throws IOException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONObject(requestBody);
            response.handlePost("customers", jsonObject);
        }

        private void handlePostSubResource(HttpExchange exchange, String subResource, Response response) throws IOException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONObject(requestBody);

            if (subResource.equalsIgnoreCase("cards")) {
                response.handlePost("cards", jsonObject);
            } else if (subResource.equalsIgnoreCase("subscription")) {
                response.handlePost("subscription", jsonObject);
            } else {
                response.send(400, "{\"status\": 400, \"message\": \"Invalid sub-resource\"}");
            }
        }

        private void handlePut(HttpExchange exchange, String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                // Handle PUT /customers/{customerId}
                int customerId = Integer.parseInt(pathSegments[2]);
                handlePutCustomer(exchange, customerId, response);
            } else {
                response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
            }
        }

        private void handlePutCustomer(HttpExchange exchange, int customerId, Response response) throws IOException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
            JSONObject jsonObject = new JSONObject(requestBody);
            response.handlePut("customers", customerId, jsonObject);
        }

        private void handleDelete(HttpExchange exchange, String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                // Handle DELETE /customers/{customerId}
                int customerId = Integer.parseInt(pathSegments[2]);
                response.handleDelete("customers", customerId);
            } else {
                response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
            }
        }
    }
}