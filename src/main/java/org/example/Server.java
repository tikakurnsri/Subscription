package org.example;

import org.json.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                        if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = 0;
                            response.handleGet("Customers", customerId, null);
                        } else if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            response.handleGet("Customers", customerId, null);
                        } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            String detail = pathSegments[3];
                            response.handleGet("Customers", customerId, detail);
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                  case "POST":
                        if (pathSegments.length == 2 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                                    .lines().collect(Collectors.joining("\n"));
                            JSONObject jsonObject = new JSONObject(requestBody);
                            response.handlePost("Customers", jsonObject);
                        } else if (pathSegments.length == 4 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            String subResource = pathSegments[3];
                            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                                    .lines().collect(Collectors.joining("\n"));
                            JSONObject jsonObject = new JSONObject(requestBody);

                            if (subResource.equalsIgnoreCase("Cards")) {
                                response.handlePost("Cards", jsonObject);
                            } else if (subResource.equalsIgnoreCase("subcription")) {
                                response.handlePost("subcription", jsonObject);
                            } else {
                                response.send(400, "{\"status\": 400, \"message\": \"Invalid sub-resource\"}");
                            }
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                    case "PUT":
                        if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                                    .lines().collect(Collectors.joining("\n"));
                            JSONObject jsonObject = new JSONObject(requestBody);
                            response.handlePut("Customers", customerId, jsonObject);
                        } else {
                            response.send(400, "{\"status\": 400, \"message\": \"Invalid path\"}");
                        }
                        break;

                    case "DELETE":
                        if (pathSegments.length == 3 && pathSegments[1].equalsIgnoreCase("Customers")) {
                            int customerId = Integer.parseInt(pathSegments[2]);
                            response.handleDelete("Customers", customerId);
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
            } catch (Exception e) {
                e.printStackTrace();
                response.send(500, "{\"status\": 500, \"message\": \"Internal server error\"}");
            }
            }
        }
    }