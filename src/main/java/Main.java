import com.sun.net.httpserver.HttpServer;
import org.example.DatabaseConnection;
import HandlerAll.CustomerHandler;
import HandlerAll.ItemHandler;
import HandlerAll.SubscriptionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final String API_KEY = "api_9000";

    public static void main(String[] args) {
        DatabaseConnection.init();

        try {
            int port = 9034;

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/customers", new CustomerHandler(API_KEY));
            server.createContext("/items", new ItemHandler(API_KEY));
            server.createContext("/subscriptions", new SubscriptionHandler(API_KEY));

            server.setExecutor(null);

            server.start();

            System.out.println("Server mulai dengan port " + port);
            System.out.println("Copy 127.0.0.1:" + port + " atau " + "http://localhost:" + port + " ke dalam Postman untuk testing!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
