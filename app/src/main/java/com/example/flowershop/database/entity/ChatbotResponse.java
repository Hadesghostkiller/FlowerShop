package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ChatbotResponse")
public class ChatbotResponse {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String keyword;
    public String response;

    public ChatbotResponse(String keyword, String response) {
        this.keyword = keyword;
        this.response = response;
    }
}
