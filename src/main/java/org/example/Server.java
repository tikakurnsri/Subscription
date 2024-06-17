package org.example;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
        startServer();
    }

    private void startServer() {
        // Implementasi logika untuk memulai server di sini
        System.out.println("Server started on port: " + port);
    }
}

