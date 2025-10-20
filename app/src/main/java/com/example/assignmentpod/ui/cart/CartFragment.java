package com.example.assignmentpod.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.cart.CartItem;

import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private TextView emptyCartText;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        observeData();
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_cart);
        emptyCartText = view.findViewById(R.id.tv_empty_cart);
    }
    
    private void setupRecyclerView() {
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartItemClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);
    }
    
    private void observeData() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }
    
    private void updateCartItems(List<CartItem> cartItems) {
        cartAdapter.setCartItems(cartItems);
        
        if (cartItems == null || cartItems.isEmpty()) {
            emptyCartText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyCartText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onItemClick(CartItem cartItem) {
        // Navigate to product detail page
        Bundle bundle = new Bundle();
        bundle.putInt("roomId", cartItem.getRoomId());
        
        // Navigate to room type detail fragment
        Navigation.findNavController(requireView())
                .navigate(R.id.action_cartFragment_to_roomTypeDetailFragment, bundle);
    }
    
    @Override
    public void onDeleteClick(CartItem cartItem) {
        cartViewModel.removeFromCart(cartItem);
        Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
    }
}
