package com.example.flowershop.model;

import com.google.gson.annotations.SerializedName;

public class SupabaseFlower {
    @SerializedName("id")
    public int id;

    @SerializedName("flower_name")
    public String flowerName;

    @SerializedName("price")
    public double price;

    @SerializedName("stock")
    public int stock;

    @SerializedName("image_resource")
    public String imageResource;

    @SerializedName("category")
    public String category;

    public SupabaseFlower() {}

    public com.example.flowershop.database.entity.Flower toFlower() {
        return new com.example.flowershop.database.entity.Flower(
                id, flowerName, price, imageResource, category, stock
        );
    }
}