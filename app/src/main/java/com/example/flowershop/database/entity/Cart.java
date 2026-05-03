package com.example.flowershop.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "Cart",
    foreignKeys = {
        @ForeignKey(
            entity = Account.class,
            parentColumns = {"username"},
            childColumns = {"username"},
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("username"), @Index("flowerID")}
)
public class Cart {
    @PrimaryKey(autoGenerate = true)
    public int cartID;
    public String username;
    public int flowerID;
    public int quantity;

    public Cart() {}

    @Ignore
    public Cart(String username, int flowerID, int quantity) {
        this.username = username;
        this.flowerID = flowerID;
        this.quantity = quantity;
    }
}