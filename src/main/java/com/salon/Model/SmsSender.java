package com.salon.Model;

public class SmsSender {
    private String number;
    private String message;

    public SmsSender(String number, String message) {
        this.number = number;
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }
}
