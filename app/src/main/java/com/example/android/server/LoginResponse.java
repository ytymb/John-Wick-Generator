package com.example.android.server;

public class LoginResponse {


    private int resultCode;

    private String messageType;

    private String messageText;

    private String variant;

    private String title;
    private String task;
    private String data;

    public int getResultCode() {
        return resultCode;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getVariant() {
        return variant;
    }

    public String getTitle() {
        return title;
    }

    public String getTask() {
        return task;
    }

    public String getData() {
        return data;
    }
}