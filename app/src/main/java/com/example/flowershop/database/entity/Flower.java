package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Flower")
public class Flower {
    @PrimaryKey(autoGenerate = true)
    public int flowerID;
    public String flowerName;
    public double price;
    public String imageResource;
    public String category;
    public int stock;

    public Flower(String flowerName, double price, String imageResource, String category, int stock) {
        this.flowerID = 0;
        this.flowerName = flowerName;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.stock = stock;
    }
}
