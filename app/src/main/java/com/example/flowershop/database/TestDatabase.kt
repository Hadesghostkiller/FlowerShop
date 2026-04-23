package com.example.flowershop.database

import com.example.flowershop.database.entity.Account
import com.example.flowershop.database.entity.Flower
import com.example.flowershop.database.entity.Cart
import com.example.flowershop.database.entity.Order

fun main() {
    println("=== Test Room Database ===")
    
    // Test Account
    val account = Account(
        username = "admin",
        password = "admin123",
        fullname = "Nguyen Van Admin",
        role = "admin"
    )
    println("Account: ${account.username} - ${account.fullname}")
    
    // Test Flower
    val flower = Flower(
        flowerName = "Hoa Hong",
        price = 50000.0,
        imageResource = "hoa_hong",
        category = "Hoa cuoi",
        stock = 100
    )
    println("Flower: ${flower.flowerName} - ${flower.price} VND")
    
    // Test Cart
    val cart = Cart(
        username = "admin",
        flowerID = 1,
        quantity = 2
    )
    println("Cart: User=${cart.username}, FlowerID=${cart.flowerID}, Qty=${cart.quantity}")
    
    // Test Order
    val order = Order(
        username = "admin",
        orderDate = "2026-04-23",
        totalMoney = 100000.0,
        status = "pending"
    )
    println("Order: ${order.orderID} - ${order.totalMoney} VND - ${order.status}")
    
    println("=== All entities created successfully ===")
}