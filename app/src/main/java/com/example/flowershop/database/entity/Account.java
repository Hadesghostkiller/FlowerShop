package com.example.flowershop.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Account")
public class Account {
    @PrimaryKey
    @NonNull
    public String username;
    public String password;
    public String fullname;
    public String role;

    public Account(@NonNull String username, String password, String fullname, String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }
}
