package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "OrderTB",
    foreignKeys = {
        @ForeignKey(
            entity = Account.class,
            parentColumns = {"username"},
            childColumns = {"username"},
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("username")}
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int orderID;
    public String username;
    public String orderDate;
    public double totalMoney;
    public String status;

    public Order(String username, String orderDate, double totalMoney, String status) {
        this.orderID = 0;
        this.username = username;
        this.orderDate = orderDate;
        this.totalMoney = totalMoney;
        this.status = status;
    }
}
