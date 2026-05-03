package com.example.flowershop.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.flowershop.database.entity.CardTemplate;
import java.util.List;

@Dao
public interface CardTemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CardTemplate template);

    @Query("SELECT * FROM CardTemplate WHERE occasion = :occasion")
    CardTemplate getByOccasion(String occasion);

    @Query("SELECT * FROM CardTemplate")
    List<CardTemplate> getAllTemplates();
}
