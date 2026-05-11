package com.example.flowershop.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String user_id;
    private int flower_id;
    private int quantity;
    private String date_time;
    private String card_mess;
    private String instructions;
    private String ma_don_hang; // New field
    private SupabaseFlower flowers;

    // Biến để lưu trạng thái tick chọn (Mặc định là false - chưa chọn)
    private boolean isSelected = false;

    public CartItem() {
    }

    public int getId() { return id; }
    public void setId(int id) { 
        this.id = id;
        this.ma_don_hang = String.format("%06d", id);
    }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public int getFlower_id() { return flower_id; }
    public void setFlower_id(int flower_id) { this.flower_id = flower_id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDate_time() { return date_time; }
    public void setDate_time(String date_time) { this.date_time = date_time; }

    public String getCard_mess() { return card_mess; }
    public void setCard_mess(String card_mess) { this.card_mess = card_mess; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getMa_don_hang() {
        if (ma_don_hang == null && id > 0) {
            ma_don_hang = String.format("%06d", id);
        }
        return ma_don_hang;
    }
    public void setMa_don_hang(String ma_don_hang) { this.ma_don_hang = ma_don_hang; }

    public SupabaseFlower getFlowers() { return flowers; }
    public void setFlowers(SupabaseFlower flowers) { this.flowers = flowers; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}