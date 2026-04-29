package com.example.flowershop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.flowershop.database.entity.Flower;

import java.util.List;

@Dao
public interface FlowerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Flower flower);

    @Update
    void update(Flower flower);

    @Delete
    void delete(Flower flower);

    @Query("SELECT * FROM Flower WHERE flowerID = :id")
    Flower getFlowerById(int id);

    @Query("SELECT * FROM Flower")
    LiveData<List<Flower>> getAllFlowers();

    @Query("SELECT * FROM Flower")
    List<Flower> getAllFlowersSync();

    @Query("SELECT * FROM Flower WHERE category = :category")
    LiveData<List<Flower>> getFlowersByCategory(String category);

    @Query("SELECT * FROM Flower WHERE category = :category")
    List<Flower> getFlowersByCategorySync(String category);

    @Query("SELECT * FROM Flower WHERE flowerName LIKE '%' || :name || '%'")
    LiveData<List<Flower>> searchFlowers(String name);
}
