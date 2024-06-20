package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class Server {
    private final int port;

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
                        handleGetCustomer(pathSegments, response);
                        break;
                    case "POST":
                        handlePostCustomer(exchange, pathSegments, response);
                        break;
                    case "PUT":
                        handlePutCustomer(exchange, pathSegments, response);
                        break;
                    case "DELETE":
                        handleDeleteCustomer(pathSegments, response);
                        break;
                    default:
                        response.send(405, createErrorResponse(405, "Method Not Allowed"));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.send(500, createErrorResponse(500, "Internal Server Error"));
            }
        }

        private void handleGetCustomer(String[] pathSegments, Response response) throws IOException, SQLException {
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
                response.send(400, createErrorResponse(400, "Invalid path"));
            }
        }

        private void handlePostCustomer(HttpExchange exchange, String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                String requestBody = readRequestBody(exchange);
                JSONObject jsonObject = new JSONObject(requestBody);
                response.handlePost("customers", jsonObject);
            } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("customers")) {
                String subResource = pathSegments[3];
                handlePostSubResource(exchange, subResource, response);
            } else {
                response.send(400, createErrorResponse(400, "Invalid path"));
            }
        }

        private void handlePostSubResource(HttpExchange exchange, String subResource, Response response) throws IOException {
            String requestBody = readRequestBody(exchange);
            JSONObject jsonObject = new JSONObject(requestBody);

            if (subResource.equalsIgnoreCase("cards")) {
                response.handlePost("cards", jsonObject);
            } else if (subResource.equalsIgnoreCase("subscription")) {
                response.handlePost("subscription", jsonObject);
            } else {
                response.send(400, createErrorResponse(400, "Invalid sub-resource"));
            }
        }

        private void handlePutCustomer(HttpExchange exchange, String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                int customerId = Integer.parseInt(pathSegments[2]);
                String requestBody = readRequestBody(exchange);
                JSONObject jsonObject = new JSONObject(requestBody);
                response.handlePut("customers", customerId, jsonObject);
            } else {
                response.send(400, createErrorResponse(400, "Invalid path"));
            }
        }

        private void handleDeleteCustomer(String[] pathSegments, Response response) throws IOException {
            if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                int customerId = Integer.parseInt(pathSegments[2]);
                response.handleDelete("customers", customerId);
            } else {
                response.send(400, createErrorResponse(400, "Invalid path"));
            }
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            return new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
        }

        private String createErrorResponse(int statusCode, String message) {
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", statusCode);
            responseJson.put("message", message);
            responseJson.put("data", JSONObject.NULL);
            return responseJson.toString();
        }
    }
}
