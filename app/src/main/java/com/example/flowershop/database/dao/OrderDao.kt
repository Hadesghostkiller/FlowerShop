package com.example.flowershop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flowershop.database.entity.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order): Long

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("SELECT * FROM OrderTB WHERE orderID = :id")
    suspend fun getOrderById(id: Int): Order?

    @Query("SELECT * FROM OrderTB WHERE username = :username")
    fun getOrdersByUsername(username: String): Flow<List<Order>>

    @Query("SELECT * FROM OrderTB")
    fun getAllOrders(): Flow<List<Order>>

    @Query("UPDATE OrderTB SET status = :status WHERE orderID = :orderId")
    suspend fun updateStatus(orderId: Int, status: String)
}