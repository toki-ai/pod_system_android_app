package com.example.assignmentpod.ui.tab.home;

import static com.example.assignmentpod.utils.Utils.formatPrice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;

public class PaymentFragment extends Fragment {

    private static final String TAG = "PaymentFragment";

    // UI Components
    private TextView tvRoomName, tvBookedDate, tvBookedSlot, tvBookedPackage, tvRoomPrice, tvRoomAddress, tvSelectedRooms;
    private TextView tvTotalRoomsPrice, tvDiscountAmount, tvTotalPrice;
    private Button btnCancel, btnConfirmPayment;
    private LinearLayout layoutZaloPay, layoutMoMo;
    private ImageView ivRoomImage;

    // Data variables
    private String roomTypeAddress, roomTypeName, roomName, bookedDate, bookedSlot, bookedPackage;
    //TODO: roomName should be a list of selection from the detail
    private int initialPrice, discountPercentage;
    private float totalPrice;
    private String selectedPaymentMethod = "";

    public PaymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            // Extract data from arguments
            roomTypeAddress = args.getString("roomTypeAddress", "Unknown Address");
            roomTypeName = args.getString("roomTypeName", "Unknown Room Type");
            roomName = args.getString("roomName", "Unknown Room");
            bookedDate = args.getString("selectedDate", "Unknown Date");
            bookedSlot = args.getString("selectedSlot", "Unknown Slot");
            bookedPackage = args.getString("selectedPackage", "Unknown Package");
            initialPrice = args.getInt("roomTypePrice", 0);
            discountPercentage = args.getInt("discountPercentage", 0);
            totalPrice = args.getFloat("totalPrice", 0.0f);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initViews(view);
            setupClickListeners();
            populateData();
            Log.d(TAG, "PaymentFragment setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    private void initViews(View view) {
        // Room image
        ivRoomImage = view.findViewById(R.id.room_image);

        // Room details
        tvRoomName = view.findViewById(R.id.room_name);
        tvRoomPrice = view.findViewById(R.id.room_price);
        tvRoomAddress = view.findViewById(R.id.room_address);
        tvBookedDate = view.findViewById(R.id.booked_date);
        tvBookedSlot = view.findViewById(R.id.booked_slot);
        tvSelectedRooms = view.findViewById(R.id.selected_rooms);
        tvBookedPackage = view.findViewById(R.id.booked_package);

        // Price breakdown
        tvTotalRoomsPrice = view.findViewById(R.id.total_rooms_price);
        tvDiscountAmount = view.findViewById(R.id.discount_amount);
        tvTotalPrice = view.findViewById(R.id.total_price);

        // Buttons
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirmPayment = view.findViewById(R.id.btn_confirm_payment);

        // Payment method layouts
        layoutZaloPay = view.findViewById(R.id.layout_zalopay);
        layoutMoMo = view.findViewById(R.id.layout_momo);
    }

    private void setupClickListeners() {
        // Payment method selection
        if (layoutZaloPay != null) {
            layoutZaloPay.setOnClickListener(v -> selectPaymentMethod("ZaloPay"));
        }

        if (layoutMoMo != null) {
            layoutMoMo.setOnClickListener(v -> selectPaymentMethod("MoMo"));
        }

        // Action buttons
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.popBackStack();
            });
        }

        if (btnConfirmPayment != null) {
            btnConfirmPayment.setOnClickListener(v -> handleConfirmPayment());
        }
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        // Reset all payment method backgrounds
        if (layoutZaloPay != null) {
            layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        if (layoutMoMo != null) {
            layoutMoMo.setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        // Highlight selected method
        if (method.equals("ZaloPay") && layoutZaloPay != null) {
            layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else if (method.equals("MoMo") && layoutMoMo != null) {
            layoutMoMo.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }

        Toast.makeText(getContext(), "Selected: " + method, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void populateData() {
        // Set room information
        if (tvRoomName != null) {
            tvRoomName.setText(roomName);
        }
        if (tvRoomPrice != null) {
            tvRoomPrice.setText(formatPrice(initialPrice));
        }
        if (tvRoomAddress != null) {
            tvRoomAddress.setText(roomTypeAddress);
        }
        if (tvBookedDate != null) {
            tvBookedDate.setText(bookedDate);
        }
        if (tvBookedSlot != null) {
            tvBookedSlot.setText(bookedSlot);
        }
        if (tvSelectedRooms != null) {
            tvSelectedRooms.setText(roomName);
        }
        if (tvBookedPackage != null) {
            tvBookedPackage.setText(bookedPackage);
        }

        // Set price breakdown
        if (tvTotalRoomsPrice != null) {
            System.out.println("tong gia cac phong la: " + initialPrice);
            tvTotalRoomsPrice.setText(formatPrice(initialPrice));
            //TODO: should the total price of selected rooms in the cart
        }
        if (tvDiscountAmount != null) {
            float discountAmount = ((float) initialPrice * (float) discountPercentage) / 100;
            tvDiscountAmount.setText("-" + formatPrice(discountAmount));
        }
        if (tvTotalPrice != null) {
            tvTotalPrice.setText(formatPrice(totalPrice));
        }
    }

    private void handleConfirmPayment() {
        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle payment confirmation based on selected method
        if (selectedPaymentMethod.equals("ZaloPay")) {
            handleZaloPayPayment();
        } else if (selectedPaymentMethod.equals("MoMo")) {
            handleMoMoPayment();
        }
    }

    private void handleZaloPayPayment() {
        // TODO: Implement ZaloPay integration
        Toast.makeText(getContext(), "ZaloPay payment initiated", Toast.LENGTH_LONG).show();
        // Here you would typically:
        // 1. Initialize ZaloPay SDK
        // 2. Create payment request
        // 3. Handle payment response
    }

    private void handleMoMoPayment() {
        // TODO: Implement MoMo integration
        Toast.makeText(getContext(), "MoMo payment initiated", Toast.LENGTH_LONG).show();
        // Here you would typically:
        // 1. Initialize MoMo SDK
        // 2. Create payment request
        // 3. Handle payment response
    }
}