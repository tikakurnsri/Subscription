package org.example;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private int port;

    public Server(int port) throws IOException {
        this.port = port;
        startServer();
    }

    private void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = server.createContext("/customers", new HandlerGetCustomer());
        context = server.createContext("/customers", new HandlerPostCustomer());
        server.createContext("/customers", new HandlerDeleteCustomer());
        server.setExecutor(null); // Default executor
        server.start();
        System.out.printf("Server started on port %d...\n", port);
    }
    private class HandlerPostCustomer implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }
    private class HandlerDeleteCustomer implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }
}


