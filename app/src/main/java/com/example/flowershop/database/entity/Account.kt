package com.example.flowershop.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Account")
data class Account(
    @PrimaryKey
    val username: String,
    val password: String,
    val fullname: String,
    val role: String
)