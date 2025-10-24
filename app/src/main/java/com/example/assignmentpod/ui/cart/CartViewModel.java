package com.example.assignmentpod.ui.cart;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.model.cart.CartItem;
import com.example.assignmentpod.model.room.Room;

import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final LiveData<List<CartItem>> cartItems;
    private final LiveData<Integer> cartItemCount;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = CartRepository.getInstance(application);
        cartItems = cartRepository.getAllCartItems();
        cartItemCount = cartRepository.getCartItemCount();
    }
    
    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }
    
    public LiveData<Integer> getCartItemCount() {
        return cartItemCount;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public void addToCart(Room room) {
        try {
            cartRepository.addToCart(room);
        } catch (Exception e) {
            errorMessage.setValue("Failed to add item to cart: " + e.getMessage());
        }
    }
    
    public void removeFromCart(CartItem cartItem) {
        try {
            cartRepository.removeFromCart(cartItem);
        } catch (Exception e) {
            errorMessage.setValue("Failed to remove item from cart: " + e.getMessage());
        }
    }
    
    public void removeFromCart(int roomId) {
        try {
            cartRepository.removeFromCart(roomId);
        } catch (Exception e) {
            errorMessage.setValue("Failed to remove item from cart: " + e.getMessage());
        }
    }
    
    public void clearCart() {
        try {
            cartRepository.clearCart();
        } catch (Exception e) {
            errorMessage.setValue("Failed to clear cart: " + e.getMessage());
        }
    }
    
    public LiveData<Boolean> isRoomInCart(int roomId) {
        return cartRepository.isRoomInCart(roomId);
    }
}
