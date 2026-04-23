package com.example.flowershop.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Cart",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Flower::class,
            parentColumns = ["flowerID"],
            childColumns = ["flowerID"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("username"),
        Index("flowerID")
    ]
)
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val cartID: Int = 0,
    val username: String,
    val flowerID: Int,
    val quantity: Int
)