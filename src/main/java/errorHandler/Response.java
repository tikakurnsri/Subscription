package errorHandler;

import org.json.JSONObject;

public class Response {

    public static String create(String message) {
        JSONObject json = new JSONObject();
        json.put("error", message);
        return json.toString();
    }
}
