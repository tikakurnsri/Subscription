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
        int statusCode = result.getStatus();

        if (result.isSukses()) {
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", statusCode);
            responseJson.put("message", result.getPesan());
            responseJson.put("data", result.getData());
            this.send(statusCode, responseJson.toString());
        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", statusCode);
            responseJson.put("message", result.getPesan());
            this.send(statusCode, responseJson.toString());
        }
    }

    public void handlePost(String tableName, JSONObject jsonObject) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            fieldKeys.append(key).append(",");
            fieldValues.append("'").append(jsonObject.getString(key)).append("',");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);
        fieldValues.deleteCharAt(fieldValues.length() - 1);

        Result result = database.insertToTable(tableName, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatus();

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", statusCode);
        responseJson.put("message", result.getPesan());
        responseJson.put("data", result.getData());

        this.send(statusCode, responseJson.toString());
    }

    public void handlePut(String tableName, int id, JSONObject jsonObject) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            fieldKeys.append(key).append("='").append(jsonObject.getString(key)).append("',");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);

        Result result = database.updateTable(tableName, id, fieldKeys.toString());
        int statusCode = result.getStatus();

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", statusCode);
        responseJson.put("message", result.getPesan());
        responseJson.put("data", result.getData());

        this.send(statusCode, responseJson.toString());
    }

    public void handleDelete(String tableName, int id) throws IOException {
        Result result = database.deleteTable(tableName, id);
        int statusCode = result.getStatus();

        JSONObject responseJson = new JSONObject();
        responseJson.put("status", statusCode);
        responseJson.put("message", result.getPesan());
        responseJson.put("data", result.getData());

        this.send(statusCode, responseJson.toString());
    }

    public void send(int statusCode, String jsonMessage) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonMessage.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(jsonMessage.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
