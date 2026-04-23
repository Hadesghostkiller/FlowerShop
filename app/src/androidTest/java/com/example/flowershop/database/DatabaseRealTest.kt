package com.example.flowershop.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.flowershop.database.dao.AccountDao
import com.example.flowershop.database.dao.FlowerDao
import com.example.flowershop.database.dao.CartDao
import com.example.flowershop.database.dao.OrderDao
import com.example.flowershop.database.entity.Account
import com.example.flowershop.database.entity.Flower
import com.example.flowershop.database.entity.Cart
import com.example.flowershop.database.entity.Order
import org.junit After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DatabaseRealTest {
    private lateinit var database: FlowerDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var flowerDao: FlowerDao
    private lateinit var cartDao: CartDao
    private lateinit var orderDao: OrderDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.databaseBuilder(
            context,
            FlowerDatabase::class.java,
            "test_flower_db"
        ).build()
        accountDao = database.accountDao()
        flowerDao = database.flowerDao()
        cartDao = database.cartDao()
        orderDao = database.orderDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAccount() {
        val account = Account("admin", "admin123", "Nguyen Van Admin", "admin")
        accountDao.insert(account)
        
        val result = accountDao.getAccountByUsername("admin")
        assertNotNull(result)
        assertEquals("admin", result?.username)
        assertEquals("Nguyen Van Admin", result?.fullname)
    }

    @Test
    fun testInsertFlower() {
        val flower = Flower(
            flowerName = "Hoa Hong",
            price = 50000.0,
            imageResource = "hoa_hong",
            category = "Hoa Cuoi",
            stock = 100
        )
        flowerDao.insert(flower)
        
        val result = flowerDao.getFlowerById(1)
        assertNotNull(result)
        assertEquals("Hoa Hong", result?.flowerName)
        assertEquals(50000.0, result?.price, 0.0)
    }

    @Test
    fun testInsertCart() {
        val account = Account("user1", "pass123", "User One", "customer")
        accountDao.insert(account)
        
        val flower = Flower(
            flowerName = "Hoa Lan",
            price = 75000.0,
            imageResource = "hoa_lan",
            category = "Hoa Cuoi",
            stock = 50
        )
        flowerDao.insert(flower)
        
        val cart = Cart(
            username = "user1",
            flowerID = 1,
            quantity = 2
        )
        cartDao.insert(cart)
        
        val result = cartDao.getCartItem("user1", 1)
        assertNotNull(result)
        assertEquals(2, result?.quantity)
    }

    @Test
    fun testInsertOrder() {
        val account = Account("user2", "pass456", "User Two", "customer")
        accountDao.insert(account)
        
        val order = Order(
            username = "user2",
            orderDate = "2026-04-23",
            totalMoney = 150000.0,
            status = "pending"
        )
        orderDao.insert(order)
        
        val result = orderDao.getOrderById(1)
        assertNotNull(result)
        assertEquals("pending", result?.status)
        assertEquals(150000.0, result?.totalMoney, 0.0)
    }
}