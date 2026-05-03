package com.example.flowershop.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.flowershop.database.dao.AccountDao;
import com.example.flowershop.database.dao.CartDao;
import com.example.flowershop.database.dao.FlowerDao;
import com.example.flowershop.database.dao.OrderDao;
import com.example.flowershop.database.entity.Account;
import com.example.flowershop.database.entity.Cart;
import com.example.flowershop.database.entity.Flower;
import com.example.flowershop.database.entity.Order;

import java.util.List;

@Database(
    entities = {Account.class, Flower.class, Cart.class, Order.class},
    version = 1,
    exportSchema = false
)
public abstract class FlowerDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();
    public abstract FlowerDao flowerDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();

    private static volatile FlowerDatabase INSTANCE;

    public static FlowerDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (FlowerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        FlowerDatabase.class,
                        "flowershop_db"
                    )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    public void populateInitialData() {
        AccountDao accountDao = accountDao();

        List<Account> accounts = accountDao.getAllAccountsSync();
        if (accounts == null || accounts.isEmpty()) {
            accountDao.insert(new Account("admin", "admin123", "Quan Ly", "admin"));
            accountDao.insert(new Account("user1", "123456", "Khach Hang", "customer"));
        }
    }
}