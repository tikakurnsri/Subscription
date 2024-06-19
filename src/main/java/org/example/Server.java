package org.example;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/customers", new CustomerHandler());
        server.setExecutor(null); // Default executor
        server.start();
        System.out.printf("Server started on port %d...\n", port);
    }

    private static class CustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetCustomer(exchange);
                    break;
                case "POST":
                    handlePostCustomer(exchange);
                    break;
                case "PUT":
                    handlePutCustomer(exchange);
                    break;
                case "DELETE":
                    handleDeleteCustomer(exchange);
                    break;
                default:
                    sendResponse(exchange);
                    break;
            }
        }

        private void handlePostCustomer(HttpExchange exchange) {
        }

        private void handlePutCustomer(HttpExchange exchange) {
        }

        private void handleGetCustomer(HttpExchange exchange) {
        }

        private void handleDeleteCustomer(HttpExchange exchange) {
        }

        private void sendResponse(HttpExchange exchange) {
        }


        private static class ItemsHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String method = exchange.getRequestMethod();
                switch (method) {
                    case "GET":
                        handleGetItems(exchange);
                        break;
                    case "POST":
                        handlePostItems(exchange);
                        break;
                    case "DELETE":
                        handleDeleteItems(exchange);
                        break;
                    case "PUT":
                        //  handlePutItems(exchange);
                        break;
                    default:
                        sendResponse(exchange, 405, "Method Not Allowed");
                        break;
                }
            }

            private void handleGetItems(HttpExchange exchange) {
            }

            private void handlePostItems(HttpExchange exchange) {
            }

            private void handleDeleteItems(HttpExchange exchange) {
            }

            private void handleGetRequest(HttpExchange exchange) throws IOException {
                String response = "This is the GET response";
                sendResponse(exchange, 200, response);
            }

            private void handlePostRequest(HttpExchange exchange) throws IOException {
                String response = "This is the POST response";
                sendResponse(exchange, 200, response);
            }

            private void handleDeleteRequest(HttpExchange exchange) throws IOException {
                String response = "This is the DELETE response";
                sendResponse(exchange, 200, response);
            }

            private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
                exchange.sendResponseHeaders(statusCode, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}