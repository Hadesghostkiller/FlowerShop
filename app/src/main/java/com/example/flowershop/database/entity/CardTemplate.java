package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CardTemplate")
public class CardTemplate {
    @PrimaryKey(autoGenerate = true)
    public int templateID;
    public String occasion;
    public String template;

    public CardTemplate(String occasion, String template) {
        this.occasion = occasion;
        this.template = template;
    }
}
