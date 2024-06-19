package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class Response {
    private final HttpExchange exchange;
    private final DatabaseConnection database = new DatabaseConnection();

    public Response(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public void handleGet(String tableName, String condition) throws IOException, SQLException {
        Result result = (Result) database.selectFromTable(tableName, condition);
        int statusCode = result.getStatus();

        if (result.isSukses()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"," +
                    "\"data\": " + result.getData() +
                    "}"
            );
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"" +
                    "}"
            );
        }
    }

    public void handleGet(String tableMaster, int id, String tableDetail) throws IOException, SQLException {
        Result resultParent = (Result) database.selectFromTable(tableMaster, "id=" + id);
        String jsonResult = resultParent.getData();
        int statusCode = resultParent.getStatus();
        boolean isSukses = resultParent.isSukses();
        String message = resultParent.getPesan();

        if (!isSukses) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + message + "\"" +
                    "}");
            return;
        }

        // Handle different tableDetail values and build the JSON response
        // Same as your initial implementation...

        if (!isSukses) {
            send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + message + "\"" +
                    "}");
        } else {
            send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + message + "\"," +
                    "\"data\": " + jsonResult +
                    "}"
            );
        }
    }

    public void handlePost(String tableName, JsonNode jsonNode) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();
        StringBuilder fieldValues = new StringBuilder();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            fieldKeys.append(field.getKey());
            fieldKeys.append(",");

            fieldValues.append("'").append(field.getValue().asText()).append("'");
            fieldValues.append(",");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);
        fieldValues.deleteCharAt(fieldValues.length() - 1);

        Result result = database.insertToTable(tableName, fieldKeys.toString(), fieldValues.toString());
        int statusCode = result.getStatus();

        if (result.isSukses()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"" +
                    "}");
        }
    }

    public void handlePut(String tableName, int id, JsonNode jsonNode) throws IOException {
        StringBuilder fieldKeys = new StringBuilder();

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            fieldKeys.append(field.getKey()).append("='").append(field.getValue().asText()).append("',");
        }

        // Remove the comma (,) character at the end of the string
        fieldKeys.deleteCharAt(fieldKeys.length() - 1);

        Result result = database.updateTable(tableName, id, fieldKeys.toString());
        int statusCode = result.getStatus();

        if (result.isSukses()) {
            send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"" +
                    "}");
        }
    }

    public void handleDelete(String tableName, int id) throws IOException {
        Result result = database.deleteTable(tableName, id);
        int statusCode = result.getStatus();

        if (result.isSukses()) {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"," +
                    "\"data\": " + result.getData() +
                    "}");
        } else {
            this.send(statusCode, "{" +
                    "\"status\": " + statusCode + "," +
                    "\"message\": \"" + result.getPesan() + "\"" +
                    "}");
        }
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
