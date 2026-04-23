package com.example.flowershop.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.flowershop.database.dao.AccountDao
import com.example.flowershop.database.dao.CartDao
import com.example.flowershop.database.dao.FlowerDao
import com.example.flowershop.database.dao.OrderDao
import com.example.flowershop.database.entity.Account
import com.example.flowershop.database.entity.Cart
import com.example.flowershop.database.entity.Flower
import com.example.flowershop.database.entity.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Account::class, Flower::class, Cart::class, Order::class],
    version = 1,
    exportSchema = false
)
abstract class FlowerDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun flowerDao(): FlowerDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: FlowerDatabase? = null

        fun getDatabase(context: Context): FlowerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlowerDatabase::class.java,
                    "flower_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: FlowerDatabase) {
            val accountDao = database.accountDao()
            val flowerDao = database.flowerDao()
            val cartDao = database.cartDao()
            val orderDao = database.orderDao()

            accountDao.insert(Account("admin", "admin123", "Nguyen Van Admin", "admin"))
            accountDao.insert(Account("user1", "pass123", "Tran Thi Hoa", "customer"))
            accountDao.insert(Account("user2", "pass456", "Le Van Binh", "customer"))

            flowerDao.insert(Flower(flowerName = "Hoa Hong Do", price = 50000.0, imageResource = "hoa_hong", category = "Hoa Cuoi", stock = 50))
            flowerDao.insert(Flower(flowerName = "Hoa Lan Trang", price = 75000.0, imageResource = "hoa_lan", category = "Hoa Lan", stock = 30))
            flowerDao.insert(Flower(flowerName = "Hoa Cuc Vang", price = 40000.0, imageResource = "hoa_cuc", category = "Hoa Dai", stock = 100))
            flowerDao.insert(Flower(flowerName = "Hoa Da Mai", price = 60000.0, imageResource = "hoa_damai", category = "Hoa Cuoi", stock = 25))
            flowerDao.insert(Flower(flowerName = "Hoa Sen Tim", price = 35000.0, imageResource = "hoa_sentim", category = "Hoa Nuoc", stock = 80))

            cartDao.insert(Cart(username = "user1", flowerID = 1, quantity = 2))
            cartDao.insert(Cart(username = "user1", flowerID = 3, quantity = 1))

            orderDao.insert(Order(username = "user1", orderDate = "2026-04-20", totalMoney = 90000.0, status = "completed"))
            orderDao.insert(Order(username = "user2", orderDate = "2026-04-22", totalMoney = 60000.0, status = "pending"))
        }
    }
}