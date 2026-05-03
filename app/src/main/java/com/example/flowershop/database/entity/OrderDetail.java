package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "OrderDetail",
    foreignKeys = {
        @ForeignKey(
            entity = Order.class,
            parentColumns = {"orderID"},
            childColumns = {"orderID"},
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Flower.class,
            parentColumns = {"flowerID"},
            childColumns = {"flowerID"},
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("orderID"), @Index("flowerID")}
)
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    public int detailID;
    public int orderID;
    public int flowerID;
    public int quantity;
    public double price;

    public OrderDetail(int orderID, int flowerID, int quantity, double price) {
        this.orderID = orderID;
        this.flowerID = flowerID;
        this.quantity = quantity;
        this.price = price;
    }
}
