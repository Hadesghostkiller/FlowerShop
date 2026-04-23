package com.example.flowershop.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OrderTB",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("username")]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderID: Int = 0,
    val username: String,
    val orderDate: String,
    val totalMoney: Double,
    val status: String
)