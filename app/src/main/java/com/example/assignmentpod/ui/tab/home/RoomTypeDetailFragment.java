package com.example.assignmentpod.ui.tab.home;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.room.RoomType;
import com.google.android.material.button.MaterialButton;

public class RoomTypeDetailFragment extends Fragment {
    
    private TextView tvRoomTypeName;
    private TextView tvRoomTypePrice;
    private MaterialButton btnBack, btnAddToCart;
    private CartRepository cartRepository;
    
    private int roomTypeId;
    private int roomId;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_type_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        cartRepository = CartRepository.getInstance(requireContext());
        
        initViews(view);
        setupClickListeners();
        
        // Get arguments if any
        Bundle args = getArguments();
        if (args != null) {
            String roomTypeName = args.getString("roomTypeName", "Unknown Room Type");
            int roomTypePrice = args.getInt("roomTypePrice", 0);
            roomTypeId = args.getInt("roomTypeId", 0);
            roomId = args.getInt("roomId", 0);
            
            tvRoomTypeName.setText(roomTypeName);
            tvRoomTypePrice.setText(String.format("%,d VND", roomTypePrice));
        }
    }
    
    private void initViews(View view) {
        tvRoomTypeName = view.findViewById(R.id.tv_room_type_name);
        tvRoomTypePrice = view.findViewById(R.id.tv_room_type_price);
        btnBack = view.findViewById(R.id.btn_back);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
        
        btnAddToCart.setOnClickListener(v -> {
            addToCart();
        });
    }
    
    private void addToCart() {
        // Create a mock Room object for cart
        RoomType roomType = new RoomType();
        roomType.setId(roomTypeId);
        roomType.setName(tvRoomTypeName.getText().toString());
        roomType.setPrice(Double.parseDouble(tvRoomTypePrice.getText().toString().replaceAll("[^0-9]", "")));
        
        Room room = new Room();
        room.setId(roomId > 0 ? roomId : roomTypeId);
        room.setName(tvRoomTypeName.getText().toString());
        room.setDescription("Room for booking");
        room.setImage("");
        room.setRoomType(roomType);
        
        cartRepository.addToCart(room);
        Toast.makeText(getContext(), "Added to cart!", Toast.LENGTH_SHORT).show();
    }
}