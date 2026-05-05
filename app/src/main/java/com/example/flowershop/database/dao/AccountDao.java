package com.example.flowershop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.flowershop.database.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM Account WHERE username = :username")
    Account getAccountByUsername(String username);

    @Query("SELECT * FROM Account WHERE username = :username AND password = :password")
    Account login(String username, String password);

    @Query("SELECT * FROM Account")
    LiveData<List<Account>> getAllAccounts();

    @Query("SELECT * FROM Account")
    List<Account> getAllAccountsSync();
}
