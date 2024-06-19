package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;

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
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9034), 0);
        server.createContext("/customers", new CustomerHandler());
        server.setExecutor(null); // Default executor
        server.start();
        System.out.printf("Server started on port %d...\n", port);
    }

    private static class CustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            Response response = new Response(exchange);
            String path = exchange.getRequestURI().getPath();
            String[] pathSegments = path.split("/");

            try {
                switch (method) {
                    case "GET":
                        if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                            response.handleGet("customers", null);
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
                        break;

                    case "POST":
                        if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("customers")) {
                            InputStream inputStream = exchange.getRequestBody();
                            JsonNode jsonNode = new ObjectMapper().readTree(inputStream);
                            response.handlePost("customers", jsonNode);
                        } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            String subResource = pathSegments[3];
                            InputStream inputStream = exchange.getRequestBody();
                            JsonNode jsonNode = new ObjectMapper().readTree(inputStream);

                            if (subResource.equalsIgnoreCase("cards")) {
                                response.handlePost("cards", jsonNode);
                            } else if (subResource.equalsIgnoreCase("subscriptions")) {
                                response.handlePost("subscriptions", jsonNode);
                            } else {
                                response.send(400, "{\"status\": 400, \"message\": \"Invalid sub-resource\"}");
                            }
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                    case "PUT":
                        if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            InputStream inputStream = exchange.getRequestBody();
                            JsonNode jsonNode = new ObjectMapper().readTree(inputStream);
                            response.handlePut("customers", customerId, jsonNode);
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                    case "DELETE":
                        if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            response.handleDelete("customers", customerId);
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                    default:
                        response.send(405, "{\"status\": 405, \"message\": \"Method Not Allowed\"}");
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                response.send(500, "{\"status\": 500, \"message\": \"Internal server error\"}");
            } catch (Exception e) {
                e.printStackTrace();
                response.send(500, "{\"status\": 500, \"message\": \"Internal server error\"}");
            }
        }
    }
}
