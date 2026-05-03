package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Flower")
public class Flower {
    @PrimaryKey
    public int flowerID;
    public String flowerName;
    public double price;
    public String imageResource;
    public String category;
    public int stock;

    public Flower(int flowerID, String flowerName, double price, String imageResource, String category, int stock) {
        this.flowerID = flowerID;
        this.flowerName = flowerName;
        this.price = price;
        this.imageResource = imageResource;
        this.category = category;
        this.stock = stock;
    }

    public static Flower createWithoutID(String flowerName, double price, String imageResource, String category, int stock) {
        return new Flower(0, flowerName, price, imageResource, category, stock);
    }
}