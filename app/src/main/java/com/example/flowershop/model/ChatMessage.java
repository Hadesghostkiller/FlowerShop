package com.example.flowershop.model;

public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;

    public String message;
    public int type;

    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
    }
}
