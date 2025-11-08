package com.example.assignmentpod.ui.tab.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.OrderRepository;
import com.example.assignmentpod.data.repository.UserRepository;
import com.example.assignmentpod.model.building.Building;
import com.example.assignmentpod.model.request.OrderDetailCreationRequest;
import com.example.assignmentpod.model.request.RoomWithAmenitiesDTO;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.servicepackage.ServicePackage;
import com.example.assignmentpod.model.user.Account;
import com.example.assignmentpod.model.user.UserProfile;
import com.example.assignmentpod.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentFragment extends Fragment {

    private static final String TAG = "PaymentFragment";

    // UI Components
    private TextView tvRoomTypeName, tvBookedDate, tvBookedSlot, tvBookedPackage, tvRoomPrice, tvRoomAddress, tvSelectedRooms;
    private TextView tvTotalRoomsPrice, tvDiscountAmount, tvTotalPrice;
    private Button btnCancel, btnConfirmPayment;
    private LinearLayout layoutZaloPay, layoutMoMo;
    private ImageView ivRoomImage;
    private TextView icZaloPaySelected, icMoMoSelected;

    // Data variables
    private String roomTypeAddress, roomTypeName, roomName, bookedDate, bookedSlot, bookedPackage;
    private int initialPrice, discountPercentage;
    private float totalPrice;
    private String selectedPaymentMethod = "";

    // ViewModel and Repository
    private PaymentViewModel paymentViewModel;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private UserProfile currentUser;
    
    // Additional order data received from RoomTypeDetailFragment
    private int roomTypeId;
    private int buildingId;
    private int servicePackageId;
    private String servicePackageName;
    private int servicePackageDiscountPercentage;

    public PaymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel and Repository
        paymentViewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        userRepository = new UserRepository(requireContext());
        orderRepository = new OrderRepository(requireContext());

        // Load user profile
        loadUserProfile();

        // Extract data from arguments
        Bundle args = getArguments();
        if (args != null) {
            roomTypeAddress = args.getString("roomTypeAddress", "Unknown Address");
            roomTypeName = args.getString("roomTypeName", "Unknown Room Type");
            roomName = args.getString("roomName", "Unknown Room");
            bookedDate = args.getString("selectedDate", "Unknown Date");
            bookedSlot = args.getString("selectedSlot", "Unknown Slot");
            bookedPackage = args.getString("selectedPackage", "Unknown Package");
            initialPrice = args.getInt("roomTypePrice", 0);
            discountPercentage = args.getInt("discountPercentage", 0);
            totalPrice = args.getFloat("totalPrice", 0.0f);
            roomTypeId = args.getInt("roomTypeId", 0);
            buildingId = args.getInt("buildingId", 0);
            
            // Extract complete ServicePackage data
            servicePackageId = args.getInt("servicePackageId", 0);
            servicePackageName = args.getString("servicePackageName", "Unknown Package");
            servicePackageDiscountPercentage = args.getInt("servicePackageDiscountPercentage", 0);
        }
    }

    private void loadUserProfile() {
        userRepository.getUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                currentUser = userProfile;
                Log.d(TAG, "User profile loaded: " + userProfile.getName());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load user profile: " + error);
                currentUser = null;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initViews(view);
            setupClickListeners();
            observeViewModel();
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
        ivRoomImage = view.findViewById(R.id.room_image);
        tvRoomTypeName = view.findViewById(R.id.room_type_name);
        tvRoomPrice = view.findViewById(R.id.room_price);
        tvRoomAddress = view.findViewById(R.id.room_address);
        tvBookedDate = view.findViewById(R.id.booked_date);
        tvBookedSlot = view.findViewById(R.id.booked_slot);
        tvSelectedRooms = view.findViewById(R.id.selected_rooms);
        tvBookedPackage = view.findViewById(R.id.booked_package);
        tvTotalRoomsPrice = view.findViewById(R.id.total_rooms_price);
        tvDiscountAmount = view.findViewById(R.id.discount_amount);
        tvTotalPrice = view.findViewById(R.id.total_price);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirmPayment = view.findViewById(R.id.btn_confirm_payment);
        layoutZaloPay = view.findViewById(R.id.layout_zalopay);
        layoutMoMo = view.findViewById(R.id.layout_momo);
        icZaloPaySelected = view.findViewById(R.id.ic_zalopay_selected);
        icMoMoSelected = view.findViewById(R.id.ic_momo_selected);
    }

    private void setupClickListeners() {
        if (layoutZaloPay != null) {
            layoutZaloPay.setOnClickListener(v -> selectPaymentMethod("ZaloPay"));
        }

        if (layoutMoMo != null) {
            layoutMoMo.setOnClickListener(v -> selectPaymentMethod("MoMo"));
        }

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

    private void observeViewModel() {
        paymentViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                Toast.makeText(getContext(), "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();
                btnConfirmPayment.setEnabled(false);
            } else {
                btnConfirmPayment.setEnabled(true);
            }
        });

        paymentViewModel.getPaymentUrlLiveData().observe(getViewLifecycleOwner(), paymentUrl -> {
            if (paymentUrl != null) {
                openPaymentUrl(paymentUrl);
            }
        });

        paymentViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.white));
        layoutMoMo.setBackgroundColor(getResources().getColor(android.R.color.white));
        icZaloPaySelected.setVisibility(View.GONE);
        icMoMoSelected.setVisibility(View.GONE);

        if (method.equals("ZaloPay") && layoutZaloPay != null) {
            layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            icZaloPaySelected.setVisibility(View.VISIBLE);
        } else if (method.equals("MoMo") && layoutMoMo != null) {
            layoutMoMo.setBackgroundColor(0xFFF8D7DA);
            icMoMoSelected.setVisibility(View.VISIBLE);
        }

        Toast.makeText(getContext(), "Đã chọn: " + method, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void populateData() {
        if (tvRoomTypeName != null) {
            tvRoomTypeName.setText(roomTypeName);
        }
        if (tvRoomPrice != null) {
            tvRoomPrice.setText(Utils.formatPrice(initialPrice));
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
        if (tvTotalRoomsPrice != null) {
            tvTotalRoomsPrice.setText(Utils.formatPrice(initialPrice));
        }
        if (tvDiscountAmount != null) {
            float discountAmount = ((float) initialPrice * (float) discountPercentage) / 100;
            tvDiscountAmount.setText("-" + Utils.formatPrice(discountAmount));
        }
        if (tvTotalPrice != null) {
            tvTotalPrice.setText(Utils.formatPrice(totalPrice));
        }
    }

    private void handleConfirmPayment() {
        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = (int) totalPrice; // Chuyển totalPrice thành int
        if (amount < 1000) {
            Toast.makeText(getContext(), "Số tiền quá nhỏ. Tối thiểu là 1,000 VND", Toast.LENGTH_LONG).show();
            return;
        }
        if (amount > 50000000) {
            Toast.makeText(getContext(), "Số tiền quá lớn. Tối đa là 50,000,000 VND", Toast.LENGTH_LONG).show();
            return;
        }

        // Save payment data to SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("PaymentData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("orderId", "#" + System.currentTimeMillis());
        editor.putString("customerName", currentUser != null && currentUser.getName() != null ? currentUser.getName() : "Khách hàng");
        editor.putString("totalAmount", Utils.formatPrice(totalPrice));
        editor.putString("roomName", roomTypeName);
        editor.putString("roomPrice", Utils.formatPrice(initialPrice));
        editor.putString("roomAddress", roomTypeAddress);
        editor.putString("bookedDate", bookedDate);
        editor.putString("bookedSlot", bookedSlot);
        editor.putString("selectedRooms", roomName);
        editor.putString("bookedPackage", bookedPackage);
        editor.apply();

        // Create order in backend first before proceeding to payment
        createOrderAPI();
        
         if (selectedPaymentMethod.equals("ZaloPay")) {
             paymentViewModel.createZaloPayOrder(requireContext(), amount);
         } else if (selectedPaymentMethod.equals("MoMo")) {
             paymentViewModel.createMoMoOrder(requireContext(), amount);
         }
    }

    private void openPaymentUrl(String paymentUrl) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi mở trang thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToPaymentSuccess() {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("orderId", generateOrderId());
            String customerName = currentUser != null && currentUser.getName() != null ?
                    currentUser.getName() : "Khách hàng";
            bundle.putString("customerName", customerName);
            bundle.putString("totalAmount", tvTotalPrice.getText().toString());
            bundle.putString("roomName", tvRoomTypeName.getText().toString());
            bundle.putString("roomPrice", tvRoomPrice.getText().toString());
            bundle.putString("roomAddress", tvRoomAddress.getText().toString());
            bundle.putString("bookedDate", tvBookedDate.getText().toString());
            bundle.putString("bookedSlot", tvBookedSlot.getText().toString());
            bundle.putString("selectedRooms", tvSelectedRooms.getText().toString());
            bundle.putString("bookedPackage", tvBookedPackage.getText().toString());

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_paymentFragment_to_paymentSuccessFragment, bundle);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to payment success: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi điều hướng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOrderId() {
        return "#" + System.currentTimeMillis();
    }

    /**
     * Create order via backend API after successful payment
     */
    private void createOrderAPI() {
        Log.d(TAG, "Creating order via API...");

        if (currentUser == null) {
            Log.e(TAG, "Cannot create order: User profile not loaded");
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Build the order request
            OrderDetailCreationRequest request = buildOrderRequest();
            
            // Call the create order API
            orderRepository.createOrder(request, new OrderRepository.CreateOrderCallback() {
                @Override
                public void onSuccess(ApiResponse<String> response) {
                    Log.d(TAG, "Order created successfully");
                    Log.d(TAG, "Status: " + response.getData());
                    Log.d(TAG, "Message: " + response.getMessage());
                    
                    String status = response.getData();
                    String message = response.getMessage();
                    
                    if ("Successfully".equals(status)) {
                        Toast.makeText(getContext(), "Đặt phòng thành công!", Toast.LENGTH_LONG).show();
                    } else if ("Pending".equals(status)) {
                        Toast.makeText(getContext(), 
                            "Đặt phòng thành công nhưng một số phòng đã được đặt: " + message, 
                            Toast.LENGTH_LONG).show();
                    }
                    
                    // Navigate to payment success screen
                    navigateToPaymentSuccess();
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to create order: " + error);
                    Toast.makeText(getContext(), 
                        "Lỗi tạo đơn hàng: " + error, 
                        Toast.LENGTH_LONG).show();
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error building order request: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Build OrderDetailCreationRequest from payment data
     */
    private OrderDetailCreationRequest buildOrderRequest() throws Exception {
        // Parse date and slot to create startTime and endTime lists
        List<String> startTimes = new ArrayList<>();
        List<String> endTimes = new ArrayList<>();
        
        // Parse bookedDate (format: dd-MM-yyyy)
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = displayFormat.parse(bookedDate);
        String apiDate = apiFormat.format(date);
        
        // Parse bookedSlot (format: "07:00 - 09:00" or multiple slots)
        String[] slots = bookedSlot.split(",");
        for (String slot : slots) {
            slot = slot.trim();
            String[] times = slot.split(" - ");
            if (times.length == 2) {
                String startTime = apiDate + "T" + times[0] + ":00";
                String endTime = apiDate + "T" + times[1] + ":00";
                startTimes.add(startTime);
                endTimes.add(endTime);
            }
        }
        
        // Create Building object with actual building ID from RoomTypeDetailFragment
        Building building = new Building();
        building.setId(buildingId);
        building.setStatus("Active"); // Set status to Active to match database schema
        building.setAddress(roomTypeAddress);
        
        // Create Room DTO with null amenities
        RoomWithAmenitiesDTO roomDTO = new RoomWithAmenitiesDTO();
        roomDTO.setId(roomTypeId); // Using roomTypeId as room ID
        roomDTO.setName(roomName);
        roomDTO.setPrice(initialPrice);
        roomDTO.setImage(""); // Empty image for now
        roomDTO.setAmenities(null); // Null amenities as requested
        
        List<RoomWithAmenitiesDTO> selectedRooms = new ArrayList<>();
        selectedRooms.add(roomDTO);
        
        // Create ServicePackage object with complete data including ID
        ServicePackage servicePackage = new ServicePackage();
        servicePackage.setId(servicePackageId);
        servicePackage.setName(servicePackageName);
        servicePackage.setDiscountPercentage(servicePackageDiscountPercentage);
        
        // Create Account object from current user
        Account customer = new Account();
        customer.setId(currentUser.getId());
        customer.setName(currentUser.getName());
        customer.setEmail(currentUser.getEmail());
        customer.setPhoneNumber(currentUser.getPhoneNumber());
        
        // Build the request
        OrderDetailCreationRequest request = new OrderDetailCreationRequest();
        request.setBuilding(building);
        request.setSelectedRooms(selectedRooms);
        request.setServicePackage(servicePackage);
        request.setCustomer(customer);
        request.setPriceRoom(initialPrice);
        request.setStartTime(startTimes);
        request.setEndTime(endTimes);
        
        Log.d(TAG, "Order request built successfully");
        Log.d(TAG, "StartTimes: " + startTimes);
        Log.d(TAG, "EndTimes: " + endTimes);
        
        return request;
    }
}