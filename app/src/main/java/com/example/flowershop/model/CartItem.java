package com.example.flowershop.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String user_id;
    private int flower_id;
    private int quantity;
    private SupabaseFlower flowers;

    // Biến để lưu trạng thái tick chọn (mặc định là true - đã chọn)
    private boolean isSelected = true;

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

    // GETTER VÀ SETTER MỚI (Lỗi của bạn nằm ở đây)
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}