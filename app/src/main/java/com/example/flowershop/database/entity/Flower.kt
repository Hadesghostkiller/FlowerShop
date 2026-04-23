package com.example.flowershop.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Flower")
data class Flower(
    @PrimaryKey(autoGenerate = true)
    val flowerID: Int = 0,
    val flowerName: String,
    val price: Double,
    val imageResource: String,
    val category: String,
    val stock: Int
)