package org.example;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
        startServer();
    }

    private void startServer() {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/customers", new HandlerGetCustomer());
        server.createContext("/customers", new HandlerPostCustomer());
        server.createContext("/customers", new HandlerDeleteCustomer());
        server.setExecutor(null); // Default executor
        server.start();
        System.out.printf("Server started on port %d...\n", port);
    }
}


