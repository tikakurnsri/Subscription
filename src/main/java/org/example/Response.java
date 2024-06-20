package org.example;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;

public class Response {
    private final HttpExchange exchange;
    private final DatabaseConnection database = new DatabaseConnection();

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void handleGet(String tableName, int customerId, String condition) throws IOException, SQLException {
        Result result = database.selectFromTable(tableName, condition);
        sendJsonResponse(result);
    }

    public void handlePost(String tableName, JSONObject jsonObject) throws IOException {
        try {
            StringBuilder fieldKeys = new StringBuilder();
            StringBuilder fieldValues = new StringBuilder();

            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                fieldKeys.append(key).append(",");
                fieldValues.append("'").append(jsonObject.getString(key)).append("',");
            }

            // Remove the trailing comma
            if (fieldKeys.length() > 0) {
                fieldKeys.deleteCharAt(fieldKeys.length() - 1);
                fieldValues.deleteCharAt(fieldValues.length() - 1);
            }

            Result result = database.insertToTable(tableName, fieldKeys.toString(), fieldValues.toString());
            sendJsonResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(500, "Internal server error");
        }
    }

    public void handlePut(String tableName, int id, JSONObject jsonObject) throws IOException {
        try {
            StringBuilder fieldKeys = new StringBuilder();

            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                fieldKeys.append(key).append("='").append(jsonObject.getString(key)).append("',");
            }

            // Remove the trailing comma
            if (fieldKeys.length() > 0) {
                fieldKeys.deleteCharAt(fieldKeys.length() - 1);
            }

            Result result = database.updateTable(tableName, id, fieldKeys.toString());
            sendJsonResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(500, "Internal server error");
        }
    }

    public void handleDelete(String tableName, int id) throws IOException {
        try {
            Result result = database.deleteTable(tableName, id);
            sendJsonResponse(result);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(500, "Internal server error");
        }
    }

    private void sendJsonResponse(Result result) throws IOException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", result.getStatus());
        responseJson.put("message", result.getPesan());
        responseJson.put("data", result.getData() != null ? result.getData() : JSONObject.NULL);
        send(result.getStatus(), responseJson.toString());
    }

    private void sendErrorResponse(int statusCode, String message) throws IOException {
        JSONObject responseJson = new JSONObject();
        responseJson.put("status", statusCode);
        responseJson.put("message", message);
        responseJson.put("data", JSONObject.NULL);
        send(statusCode, responseJson.toString());
    }

    void send(int statusCode, String jsonMessage) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonMessage.length());
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(jsonMessage.getBytes());
        }
    }
}
