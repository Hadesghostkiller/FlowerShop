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
        FlowerDao flowerDao = flowerDao();

        List<Account> accounts = accountDao.getAllAccountsSync();
        if (accounts == null || accounts.isEmpty()) {
            accountDao.insert(new Account("admin", "admin123", "Quan Ly", "admin"));
            accountDao.insert(new Account("user1", "123456", "Khach Hang", "customer"));
        }

        List<Flower> flowers = flowerDao.getAllFlowersSync();
        if (flowers == null || flowers.isEmpty()) {
            flowerDao.insert(new Flower("Hoa Hong Do", 350000, "hoa_hong", "Hoa Bo", 20));
            flowerDao.insert(new Flower("Hoa Lan Trang", 450000, "hoa_lan", "Hoa Bo", 15));
            flowerDao.insert(new Flower("Hoa Cuc Vang", 280000, "hoa_cuc", "Hoa Bo", 25));

            flowerDao.insert(new Flower("Hoa Sinh Nhat", 500000, "hoa_sinh_nhat", "Sinh Nhat", 30));
            flowerDao.insert(new Flower("Bo Hoa Chuc Mung", 650000, "hoa_chucmung", "Sinh Nhat", 20));
            flowerDao.insert(new Flower("Hoa Huong Duong", 420000, "hoa_huongduong", "Sinh Nhat", 18));

            flowerDao.insert(new Flower("Khai Truong Phat", 1200000, "hoa_kt1", "Khai Truong", 10));
            flowerDao.insert(new Flower("Chuc Mung Khai Truong", 1500000, "hoa_kt2", "Khai Truong", 8));
            flowerDao.insert(new Flower("Hoa Chuc Moc", 980000, "hoa_chucmo", "Khai Truong", 12));

            flowerDao.insert(new Flower("Vong Hoa Chia Buon", 800000, "hoa_cuoi1", "Chia Buon", 10));
            flowerDao.insert(new Flower("Hoa Tang Trieu", 650000, "hoa_tangtrieu", "Chia Buon", 8));
            flowerDao.insert(new Flower("Hoa Phong Su", 550000, "hoa_phongsu", "Chia Buon", 12));

            flowerDao.insert(new Flower("Binh Hoa Cuoi", 2500000, "hoa_cuoi", "Hoa Cuoi", 5));
            flowerDao.insert(new Flower("Bo Hoa Cuoi Dep", 1800000, "hoa_cuoi2", "Hoa Cuoi", 7));
        }
    }
}