package com.example.flowershop.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SupabaseFlower implements Serializable {
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

    @SerializedName("note")
    public String note;

    public SupabaseFlower() {}

}
