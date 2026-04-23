package com.example.flowershop.database

import org.junit.Test
import org.junit.Assert.*

class DatabaseTest {

    @Test
    fun testAccountEntity() {
        val account = com.example.flowershop.database.entity.Account(
            username = "user1",
            password = "pass123",
            fullname = "Nguyen Van A",
            role = "customer"
        )
        assertEquals("user1", account.username)
        assertEquals("Nguyen Van A", account.fullname)
    }

    @Test
    fun testFlowerEntity() {
        val flower = com.example.flowershop.database.entity.Flower(
            flowerName = "Hoa Hong",
            price = 50000.0,
            imageResource = "hoa_hong",
            category = "Hoa cuoi",
            stock = 100
        )
        assertEquals("Hoa Hong", flower.flowerName)
        assertEquals(50000.0, flower.price, 0.0)
        assertEquals(100, flower.stock)
    }

    @Test
    fun testCartEntity() {
        val cart = com.example.flowershop.database.entity.Cart(
            username = "user1",
            flowerID = 1,
            quantity = 2
        )
        assertEquals("user1", cart.username)
        assertEquals(1, cart.flowerID)
        assertEquals(2, cart.quantity)
    }

    @Test
    fun testOrderEntity() {
        val order = com.example.flowershop.database.entity.Order(
            username = "user1",
            orderDate = "2026-04-23",
            totalMoney = 100000.0,
            status = "pending"
        )
        assertEquals("user1", order.username)
        assertEquals(100000.0, order.totalMoney, 0.0)
        assertEquals("pending", order.status)
    }
}