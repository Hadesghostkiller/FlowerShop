package com.example.flowershop.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.flowershop.database.entity.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderDetail orderDetail);

    @Update
    void update(OrderDetail orderDetail);

    @Delete
    void delete(OrderDetail orderDetail);

    @Query("SELECT * FROM OrderDetail WHERE orderID = :orderID")
    List<OrderDetail> getDetailsByOrderID(int orderID);

    @Query("SELECT * FROM OrderDetail")
    List<OrderDetail> getAllOrderDetails();
}
