package com.example.flowershop.model;

import com.example.flowershop.model.SupabaseFlower;

public class CartItem {
    private int id;
    private String user_id;
    private int flower_id;
    private int quantity;

    // Khai báo biến có tên TRÙNG VỚI TÊN BẢNG (flowers) để hứng dữ liệu JOIN từ Supabase
    private SupabaseFlower flowers;

    public CartItem() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public int getFlower_id() { return flower_id; }
    public void setFlower_id(int flower_id) { this.flower_id = flower_id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public SupabaseFlower getFlowers() { return flowers; }
    public void setFlowers(SupabaseFlower flowers) { this.flowers = flowers; }
}