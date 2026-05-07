package com.example.flowershop.model;

import java.io.Serializable;

public class FavoriteItem implements Serializable {
    private long id;
    private String user_id;
    private long flower_id;
    private SupabaseFlower flowers;

    public FavoriteItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getFlower_id() {
        return flower_id;
    }

    public void setFlower_id(long flower_id) {
        this.flower_id = flower_id;
    }

    public SupabaseFlower getFlowers() {
        return flowers;
    }

    public void setFlowers(SupabaseFlower flowers) {
        this.flowers = flowers;
    }
}
