package com.example.flowershop.database

import android.util.Log
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class DatabaseWithDataTest {
    private lateinit var database: FlowerDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var flowerDao: FlowerDao
    private lateinit var cartDao: CartDao
    private lateinit var orderDao: OrderDao

    private val TAG = "DatabaseTest"

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.databaseBuilder(
            context,
            FlowerDatabase::class.java,
            "flower_shop_db"
        ).build()
        accountDao = database.accountDao()
        flowerDao = database.flowerDao()
        cartDao = database.cartDao()
        orderDao = database.orderDao()
        
        Log.i(TAG, "=== Inserting sample data ===")
        insertSampleData()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun insertSampleData() {
        // Insert Account
        accountDao.insert(Account("admin", "admin123", "Nguyen Van Admin", "admin"))
        accountDao.insert(Account("user1", "pass123", "Tran Thi Hoa", "customer"))
        accountDao.insert(Account("user2", "pass456", "Le Van Binh", "customer"))
        
        // Insert Flowers
        flowerDao.insert(Flower(flowerName = "Hoa Hong Do", price = 50000.0, imageResource = "hoa_hong", category = "Hoa Cuoi", stock = 50))
        flowerDao.insert(Flower(flowerName = "Hoa Lan Trang", price = 75000.0, imageResource = "hoa_lan", category = "Hoa Lan", stock = 30))
        flowerDao.insert(Flower(flowerName = "Hoa Cuc Vang", price = 40000.0, imageResource = "hoa_cuc", category = "Hoa Dai", stock = 100))
        flowerDao.insert(Flower(flowerName = "Hoa Da Mai", price = 60000.0, imageResource = "hoa_damai", category = "Hoa Cuoi", stock = 25))
        flowerDao.insert(Flower(flowerName = "Hoa Sen Tim", price = 35000.0, imageResource = "hoa_sentim", category = "Hoa Nuoc", stock = 80))
        
        // Insert Cart
        cartDao.insert(Cart(username = "user1", flowerID = 1, quantity = 2))
        cartDao.insert(Cart(username = "user1", flowerID = 3, quantity = 1))
        
        // Insert Orders
        orderDao.insert(Order(username = "user1", orderDate = "2026-04-20", totalMoney = 90000.0, status = "completed"))
        orderDao.insert(Order(username = "user2", orderDate = "2026-04-22", totalMoney = 60000.0, status = "pending"))
    }

    @Test
    fun testCheckData() {
        // Check Accounts
        val account = accountDao.getAccountByUsername("admin")
        assertNotNull(account)
        Log.i(TAG, "Account: ${account?.username} - ${account?.fullname}")
        
        // Check Flowers
        val flower = flowerDao.getFlowerById(1)
        assertNotNull(flower)
        Log.i(TAG, "Flower: ${flower?.flowerName} - ${flower?.price} VND")
        
        // Check Cart
        val cart = cartDao.getCartItem("user1", 1)
        assertNotNull(cart)
        Log.i(TAG, "Cart: User=${cart?.username}, FlowerID=${cart?.flowerID}, Qty=${cart?.quantity}")
        
        // Check Order
        val order = orderDao.getOrderById(1)
        assertNotNull(order)
        Log.i(TAG, "Order: ${order?.orderID} - ${order?.totalMoney} VND - ${order?.status}")
        
        Log.i(TAG, "=== All data verified ===")
    }
}