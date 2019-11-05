package com.example.restapiretrofitdemo;

public class Data {
    private String userId;
    private String name;
    private String title;
    private String body;
    private String status;

    public Data() {
    }

    public Data(String userId, String name, String title, String body, String status) {
        this.userId = userId;
        this.name = name;
        this.title = title;
        this.body = body;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getStatus() {
        return status;
    }
}
