package com.example.flowershop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flowershop.database.entity.Cart
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart): Long

    @Update
    suspend fun update(cart: Cart)

    @Delete
    suspend fun delete(cart: Cart)

    @Query("SELECT * FROM Cart WHERE username = :username")
    fun getCartByUsername(username: String): Flow<List<Cart>>

    @Query("SELECT * FROM Cart WHERE username = :username AND flowerID = :flowerId")
    suspend fun getCartItem(username: String, flowerId: Int): Cart?

    @Query("DELETE FROM Cart WHERE username = :username")
    suspend fun clearCart(username: String)

    @Query("UPDATE Cart SET quantity = :quantity WHERE cartID = :cartId")
    suspend fun updateQuantity(cartId: Int, quantity: Int)
}