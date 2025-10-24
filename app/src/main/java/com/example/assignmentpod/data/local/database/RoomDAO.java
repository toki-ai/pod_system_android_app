package com.example.assignmentpod.data.local.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.assignmentpod.model.cart.CartItem;

import java.util.List;

@Dao
public interface RoomDAO {
    
    // Cart operations
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    LiveData<List<CartItem>> getAllCartItems();
    
    @Query("SELECT COUNT(*) FROM cart_items")
    LiveData<Integer> getCartItemCount();
    
    @Query("SELECT * FROM cart_items WHERE roomId = :roomId")
    CartItem getCartItemByRoomId(int roomId);
    
    @Insert
    void insertCartItem(CartItem cartItem);
    
    @Update
    void updateCartItem(CartItem cartItem);
    
    @Delete
    void deleteCartItem(CartItem cartItem);
    
    @Query("DELETE FROM cart_items WHERE roomId = :roomId")
    void deleteCartItemByRoomId(int roomId);
    
    @Query("DELETE FROM cart_items")
    void clearCart();
}
