package com.example.flowershop.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.flowershop.database.entity.ChatbotResponse;
import java.util.List;

@Dao
public interface ChatbotResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatbotResponse response);

    @Query("SELECT * FROM ChatbotResponse WHERE keyword LIKE '%' || :keyword || '%'")
    List<ChatbotResponse> searchByKeyword(String keyword);

    @Query("SELECT * FROM ChatbotResponse")
    List<ChatbotResponse> getAllResponses();
}
