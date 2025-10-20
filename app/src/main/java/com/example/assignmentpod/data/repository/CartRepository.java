package com.example.assignmentpod.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.local.database.AppDatabase;
import com.example.assignmentpod.data.local.database.RoomDAO;
import com.example.assignmentpod.model.cart.CartItem;
import com.example.assignmentpod.model.room.Room;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {
    private static CartRepository instance;
    private final RoomDAO roomDAO;
    private final ExecutorService executor;
    private final Handler mainHandler;
    
    private CartRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        roomDAO = database.roomDAO();
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public static synchronized CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    public LiveData<List<CartItem>> getAllCartItems() {
        MutableLiveData<List<CartItem>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            List<CartItem> items = roomDAO.getAllCartItems();
            mainHandler.post(() -> liveData.setValue(items));
        });
        return liveData;
    }
    
    public LiveData<Integer> getCartItemCount() {
        MutableLiveData<Integer> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            int count = roomDAO.getCartItemCount();
            mainHandler.post(() -> liveData.setValue(count));
        });
        return liveData;
    }
    
    public void addToCart(Room room) {
        executor.execute(() -> {
            // Check if room already exists in cart
            CartItem existingItem = roomDAO.getCartItemByRoomId(room.getId());
            if (existingItem == null) {
                // Create new cart item
                CartItem cartItem = new CartItem(
                    room.getId(),
                    room.getName(),
                    room.getDescription(),
                    room.getImage(),
                    room.getRoomType() != null ? room.getRoomType().getName() : "Unknown",
                    room.getRoomType() != null ? room.getRoomType().getPrice() : 0.0
                );
                roomDAO.insertCartItem(cartItem);
            }
        });
    }
    
    public void removeFromCart(int roomId) {
        executor.execute(() -> {
            roomDAO.deleteCartItemByRoomId(roomId);
        });
    }
    
    public void removeFromCart(CartItem cartItem) {
        executor.execute(() -> {
            roomDAO.deleteCartItem(cartItem);
        });
    }
    
    public void clearCart() {
        executor.execute(() -> {
            roomDAO.clearCart();
        });
    }
    
    public LiveData<Boolean> isRoomInCart(int roomId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            CartItem item = roomDAO.getCartItemByRoomId(roomId);
            boolean isInCart = item != null;
            mainHandler.post(() -> liveData.setValue(isInCart));
        });
        return liveData;
    }
}
