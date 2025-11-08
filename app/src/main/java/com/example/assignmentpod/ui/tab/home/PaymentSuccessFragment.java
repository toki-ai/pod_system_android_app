package com.example.assignmentpod.ui.tab.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;

public class PaymentSuccessFragment extends Fragment {

    // UI Components
    private TextView tvOrderId, tvCustomerName, tvTotalAmount;
    private TextView tvRoomName, tvRoomPrice, tvRoomAddress;
    private TextView tvBookedDate, tvBookedSlot, tvSelectedRooms, tvBookedPackage;
    private ImageView ivRoomImage;
    private Button btnBackToHome;

    // Data variables
    private String orderId, customerName, totalAmount;
    private String roomName, roomPrice, roomAddress;
    private String bookedDate, bookedSlot, selectedRooms, bookedPackage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get data from arguments or SharedPreferences
        Bundle args = getArguments();
        if (args != null) {
            orderId = args.getString("orderId", "#" + System.currentTimeMillis());
            customerName = args.getString("customerName", "Khách hàng");
            totalAmount = args.getString("totalAmount", "0 VND");
            roomName = args.getString("roomName", "Phòng POD");
            roomPrice = args.getString("roomPrice", "0 VND/tiếng");
            roomAddress = args.getString("roomAddress", "Địa chỉ không xác định");
            bookedDate = args.getString("bookedDate", "Chưa xác định");
            bookedSlot = args.getString("bookedSlot", "Chưa xác định");
            selectedRooms = args.getString("selectedRooms", "Chưa xác định");
            bookedPackage = args.getString("bookedPackage", "Gói cơ bản");
        } else {
            // Fallback to SharedPreferences if no arguments provided
            loadDataFromSharedPreferences();
        }
    }

    /**
     * Load payment data from SharedPreferences as fallback
     */
    private void loadDataFromSharedPreferences() {
        if (getContext() != null) {
            android.content.SharedPreferences prefs = getContext().getSharedPreferences("PaymentData", android.content.Context.MODE_PRIVATE);
            orderId = prefs.getString("orderId", "#" + System.currentTimeMillis());
            customerName = prefs.getString("customerName", "Khách hàng");
            totalAmount = prefs.getString("totalAmount", "0 VND");
            roomName = prefs.getString("roomName", "Phòng POD");
            roomPrice = prefs.getString("roomPrice", "0 VND/tiếng");
            roomAddress = prefs.getString("roomAddress", "Địa chỉ không xác định");
            bookedDate = prefs.getString("bookedDate", "Chưa xác định");
            bookedSlot = prefs.getString("bookedSlot", "Chưa xác định");
            selectedRooms = prefs.getString("selectedRooms", "Chưa xác định");
            bookedPackage = prefs.getString("bookedPackage", "Gói cơ bản");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        populateData();
    }

    private void initViews(View view) {
        // Order summary
        tvOrderId = view.findViewById(R.id.tv_order_id);
        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        
        // Room details
        tvRoomName = view.findViewById(R.id.tv_room_name);
        tvRoomPrice = view.findViewById(R.id.tv_room_price);
        tvRoomAddress = view.findViewById(R.id.tv_room_address);
        tvBookedDate = view.findViewById(R.id.tv_booked_date);
        tvBookedSlot = view.findViewById(R.id.tv_booked_slot);
        tvSelectedRooms = view.findViewById(R.id.tv_selected_rooms);
        tvBookedPackage = view.findViewById(R.id.tv_booked_package);
        ivRoomImage = view.findViewById(R.id.room_image);
        
        // Action button
        btnBackToHome = view.findViewById(R.id.btn_back_to_home);
    }

    private void setupClickListeners() {
        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> {
                // Navigate back to home
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_paymentSuccessFragment_to_homeFragment);
            });
        }
    }

    private void populateData() {
        // Set order summary
        if (tvOrderId != null) {
            tvOrderId.setText(orderId);
        }
        if (tvCustomerName != null) {
            tvCustomerName.setText(customerName);
        }
        if (tvTotalAmount != null) {
            tvTotalAmount.setText(totalAmount);
        }
        
        // Set room details
        if (tvRoomName != null) {
            tvRoomName.setText(roomName);
        }
        if (tvRoomPrice != null) {
            tvRoomPrice.setText(roomPrice);
        }
        if (tvRoomAddress != null) {
            tvRoomAddress.setText(roomAddress);
        }
        if (tvBookedDate != null) {
            tvBookedDate.setText(bookedDate);
        }
        if (tvBookedSlot != null) {
            tvBookedSlot.setText(bookedSlot);
        }
        if (tvSelectedRooms != null) {
            tvSelectedRooms.setText(selectedRooms);
        }
        if (tvBookedPackage != null) {
            tvBookedPackage.setText(bookedPackage);
        }
    }
}
