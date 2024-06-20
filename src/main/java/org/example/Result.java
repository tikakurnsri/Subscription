package org.example;

import org.json.JSONObject;

public class Result {
    private final int status;
    private final boolean sukses;
    private final String pesan;
    private final String data;

    public Result(int status, boolean sukses, String pesan, String data) {
        this.status = status;
        this.sukses = sukses;
        this.pesan = pesan;
        this.data = data;
    }


    public int getStatus() {
        return status;
    }

    public boolean isSukses() {
        return sukses;
    }

    public String getPesan() {
        return pesan;
    }

    public String getData() {
        return data;
    }
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        jsonObject.put("sukses", sukses);
        jsonObject.put("pesan", pesan);
        jsonObject.put("data", data);
        return jsonObject.toString();
    }
}
