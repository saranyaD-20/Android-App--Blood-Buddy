package com.simats.bloodbuddy;

public class ChatMessage {
    private String message;
    private boolean sentByUser;

    public ChatMessage(String message, boolean sentByUser) {
        this.message = message;
        this.sentByUser = sentByUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return sentByUser;
    }
}