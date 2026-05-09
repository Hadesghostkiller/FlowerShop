package com.example.flowershop.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Order implements Serializable {
    @SerializedName("id")
    public String id;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("customer_name")
    public String customer_name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("address")
    public String address;

    @SerializedName("total_amount")
    public double total_amount;

    @SerializedName("payment_method")
    public String payment_method;

    @SerializedName("status")
    public String status;

    // Thêm trường này để lưu danh sách tên hoa
    @SerializedName("order_details")
    public String order_details;
}