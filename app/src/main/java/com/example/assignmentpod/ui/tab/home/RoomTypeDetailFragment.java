package com.example.assignmentpod.ui.tab.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;
import com.google.android.material.button.MaterialButton;

public class RoomTypeDetailFragment extends Fragment {
    
    private TextView tvRoomTypeName;
    private TextView tvRoomTypePrice;
    private MaterialButton btnBack;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_type_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        
        // Get arguments if any
        Bundle args = getArguments();
        if (args != null) {
            String roomTypeName = args.getString("roomTypeName", "Unknown Room Type");
            int roomTypePrice = args.getInt("roomTypePrice", 0);
            
            tvRoomTypeName.setText(roomTypeName);
            tvRoomTypePrice.setText(roomTypePrice + " VND");
        }
    }
    
    private void initViews(View view) {
        tvRoomTypeName = view.findViewById(R.id.tv_room_type_name);
        tvRoomTypePrice = view.findViewById(R.id.tv_room_type_price);
        btnBack = view.findViewById(R.id.btn_back);
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
    }
}