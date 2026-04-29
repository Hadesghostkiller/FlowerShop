package com.example.flowershop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.flowershop.database.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM OrderTB WHERE orderID = :id")
    Order getOrderById(int id);

    @Query("SELECT * FROM OrderTB WHERE username = :username")
    LiveData<List<Order>> getOrdersByUsername(String username);

    @Query("SELECT * FROM OrderTB")
    LiveData<List<Order>> getAllOrders();

    @Query("UPDATE OrderTB SET status = :status WHERE orderID = :orderId")
    void updateStatus(int orderId, String status);
}
