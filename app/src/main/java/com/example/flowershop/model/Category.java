package com.example.flowershop.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    public int id;
    
    @SerializedName("name")
    public String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
