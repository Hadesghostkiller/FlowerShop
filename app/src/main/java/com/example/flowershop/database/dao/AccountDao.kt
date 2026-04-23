package com.example.flowershop.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flowershop.database.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT * FROM Account WHERE username = :username")
    suspend fun getAccountByUsername(username: String): Account?

    @Query("SELECT * FROM Account WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): Account?

    @Query("SELECT * FROM Account")
    fun getAllAccounts(): Flow<List<Account>>
}