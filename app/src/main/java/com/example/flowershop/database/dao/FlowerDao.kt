package com.example.flowershop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flowershop.database.entity.Flower
import kotlinx.coroutines.flow.Flow

@Dao
interface FlowerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flower: Flower): Long

    @Update
    suspend fun update(flower: Flower)

    @Delete
    suspend fun delete(flower: Flower)

    @Query("SELECT * FROM Flower WHERE flowerID = :id")
    suspend fun getFlowerById(id: Int): Flower?

    @Query("SELECT * FROM Flower")
    fun getAllFlowers(): Flow<List<Flower>>

    @Query("SELECT * FROM Flower WHERE category = :category")
    fun getFlowersByCategory(category: String): Flow<List<Flower>>

    @Query("SELECT * FROM Flower WHERE flowerName LIKE '%' || :name || '%'")
    fun searchFlowers(name: String): Flow<List<Flower>>
}