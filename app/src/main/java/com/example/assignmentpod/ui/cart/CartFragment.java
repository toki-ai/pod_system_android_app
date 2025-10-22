package com.example.assignmentpod.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private View emptyCartLayout;
    private TextView tvTotalItems, tvTotalPrice;
    private MaterialButton btnCheckout;
    private ImageView btnBack;
    
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
        setupClickListeners();
        observeData();
        
        // Add entrance animation
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        view.startAnimation(slideIn);
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_cart);
        emptyCartLayout = view.findViewById(R.id.empty_cart_layout);
        tvTotalItems = view.findViewById(R.id.tv_total_items);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnBack = view.findViewById(R.id.btn_back);
    }
    
    private void setupRecyclerView() {
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartItemClickListener(this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);
        
        // Add item decoration for better spacing
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
            getContext(), LinearLayoutManager.VERTICAL));
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            // Add exit animation
            Animation slideOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
            v.startAnimation(slideOut);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    Navigation.findNavController(v).popBackStack();
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
        
        btnCheckout.setOnClickListener(v -> {
            // Add button press animation
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    Toast.makeText(getContext(), "Tính năng thanh toán sẽ có sớm!", Toast.LENGTH_SHORT).show();
                });
        });
    }
    
    private void observeData() {
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }
    
    private void updateCartItems(List<CartItem> cartItems) {
        cartAdapter.setCartItems(cartItems);
        
        if (cartItems == null || cartItems.isEmpty()) {
            emptyCartLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvTotalItems.setText("Total: 0 items");
            tvTotalPrice.setText("0 VND");
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);
        } else {
            emptyCartLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            // Calculate totals
            int totalItems = cartItems.size();
            double totalPrice = 0.0;
            for (CartItem item : cartItems) {
                totalPrice += item.getRoomPrice();
            }
            
            tvTotalItems.setText("Total: " + totalItems + " item" + (totalItems > 1 ? "s" : ""));
            tvTotalPrice.setText(String.format("%,.0f VND", totalPrice));
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
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
        Toast.makeText(getContext(), "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }
}
