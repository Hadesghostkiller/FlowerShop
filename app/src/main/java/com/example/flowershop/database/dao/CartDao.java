package com.example.flowershop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.flowershop.database.entity.Cart;

import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Cart cart);

    @Update
    void update(Cart cart);

    @Delete
    void delete(Cart cart);

    @Query("SELECT * FROM Cart WHERE username = :username")
    LiveData<List<Cart>> getCartByUsername(String username);

    @Query("SELECT * FROM Cart WHERE username = :username AND flowerID = :flowerId")
    Cart getCartItem(String username, int flowerId);

    @Query("SELECT * FROM Cart WHERE cartID = :cartId")
    Cart getCartItemSync(int cartId);

    @Query("SELECT * FROM Cart WHERE username = :username")
    List<Cart> getCartByUsernameSync(String username);

    @Query("DELETE FROM Cart WHERE username = :username")
    void clearCart(String username);

    @Query("UPDATE Cart SET quantity = :quantity WHERE cartID = :cartId")
    void updateQuantity(int cartId, int quantity);
}
