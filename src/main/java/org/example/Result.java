package org.example;

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
}
