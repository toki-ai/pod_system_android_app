package com.example.assignmentpod.ui.tab.home;

import static com.example.assignmentpod.utils.Utils.formatPrice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.example.assignmentpod.data.repository.UserRepository;
import com.example.assignmentpod.model.user.UserProfile;
import com.example.assignmentpod.zalopay.Api.CreateOrder;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

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
    //TODO: roomName should be a list of selection from the detail
    private int initialPrice, discountPercentage;
    private float totalPrice;
    private String selectedPaymentMethod = "";
    
    // User data
    private UserRepository userRepository;
    private UserProfile currentUser;

    public PaymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize UserRepository
        userRepository = new UserRepository(requireContext());
        
        // Load user profile
        loadUserProfile();
        
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
                // Set default fallback
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
        tvRoomTypeName = view.findViewById(R.id.room_type_name);
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

        // Selection icons
        icZaloPaySelected = view.findViewById(R.id.ic_zalopay_selected);
        icMoMoSelected = view.findViewById(R.id.ic_momo_selected);
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

        // Reset all payment method backgrounds and icons
        if (layoutZaloPay != null) {
            layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        if (layoutMoMo != null) {
            layoutMoMo.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        if (icZaloPaySelected != null) {
            icZaloPaySelected.setVisibility(View.GONE);
        }
        if (icMoMoSelected != null) {
            icMoMoSelected.setVisibility(View.GONE);
        }

        // Highlight selected method
        if (method.equals("ZaloPay") && layoutZaloPay != null) {
            layoutZaloPay.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            if (icZaloPaySelected != null) {
                icZaloPaySelected.setVisibility(View.VISIBLE);
            }
        } else if (method.equals("MoMo") && layoutMoMo != null) {
            // Use pink color for MoMo selection
            layoutMoMo.setBackgroundColor(0xFFF8D7DA); // Light pink background
            if (icMoMoSelected != null) {
                icMoMoSelected.setVisibility(View.VISIBLE);
            }
        }

        Toast.makeText(getContext(), "Selected: " + method, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void populateData() {
        // Set room information
        if (tvRoomTypeName != null) {
            tvRoomTypeName.setText(roomTypeName);
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
        Toast.makeText(getContext(), "ZaloPay payment initiated", Toast.LENGTH_LONG).show();
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        CreateOrder orderApi = new CreateOrder();
        try {
            // Convert float totalPrice to integer (ZaloPay requirement - no decimals allowed)
            int amountInt = (int) totalPrice;
            
            // Validate amount (ZaloPay typically requires 1,000 - 50,000,000 VND)
            if (amountInt < 1000) {
                Toast.makeText(getContext(), "Amount too small. Minimum is 1,000 VND", Toast.LENGTH_LONG).show();
                return;
            }
            if (amountInt > 50000000) {
                Toast.makeText(getContext(), "Amount too large. Maximum is 50,000,000 VND", Toast.LENGTH_LONG).show();
                return;
            }
            
            String amountString = String.valueOf(amountInt);
            
            Log.d(TAG, "Original totalPrice (float): " + totalPrice);
            Log.d(TAG, "Converted to integer: " + amountInt);
            Log.d(TAG, "Final amount string for ZaloPay: " + amountString);

            JSONObject data = orderApi.createOrder(amountString);
            Log.d(TAG, "ZaloPay response: " + data.toString());
            
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d(TAG, "Opening ZaloPay with token: " + token);
                
                ZaloPaySDK.getInstance().payOrder(getActivity(), token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String zpTransToken, String appTransId) {
                        // Navigate to success screen
                        Log.d(TAG, "Payment succeeded - transactionId: " + transactionId);
                        Log.d(TAG, "zpTransToken: " + zpTransToken);
                        Log.d(TAG, "appTransId: " + appTransId);
                        
                        // Use requireActivity().runOnUiThread to ensure we're on UI thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                navigateToPaymentSuccess();
                            });
                        }
                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransId) {
                        Log.d(TAG, "Payment cancelled - zpTransToken: " + zpTransToken + ", appTransId: " + appTransId);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Đã hủy thanh toán", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransId) {
                        Log.e(TAG, "Payment error: " + zaloPayError.toString());
                        Log.e(TAG, "zpTransToken: " + zpTransToken + ", appTransId: " + appTransId);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Thanh toán thất bại: " + zaloPayError.toString(), Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                });
            } else {
                String returnMessage = data.optString("return_message", "Unknown error");
                String subReturnMessage = data.optString("sub_return_message", "");
                String errorMsg = "Failed to create order: " + returnMessage;
                if (!subReturnMessage.isEmpty()) {
                    errorMsg += " (" + subReturnMessage + ")";
                }
                Log.e(TAG, errorMsg);
                Log.e(TAG, "Full response: " + data.toString());
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in ZaloPay payment: " + e.getMessage());
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleMoMoPayment() {
        // TODO: Implement MoMo integration
        Toast.makeText(getContext(), "MoMo payment initiated", Toast.LENGTH_LONG).show();
        // For now, simulate successful payment
        navigateToPaymentSuccess();
    }

    private void navigateToPaymentSuccess() {
        try {
            // Create bundle with booking data
            Bundle bundle = new Bundle();
            bundle.putString("orderId", generateOrderId());
            
            // Use authenticated user's name or fallback to default
            String customerName = "Khách hàng"; // Default fallback
            if (currentUser != null && currentUser.getName() != null && !currentUser.getName().isEmpty()) {
                customerName = currentUser.getName();
                Log.d(TAG, "Using authenticated user name: " + customerName);
            } else {
                Log.w(TAG, "User profile not loaded, using default customer name");
            }
            bundle.putString("customerName", customerName);
            
            // Pass all the booking information
            bundle.putString("totalAmount", tvTotalPrice != null ? tvTotalPrice.getText().toString() : "0");
            bundle.putString("roomName", tvRoomTypeName != null ? tvRoomTypeName.getText().toString() : "Unknown");
            bundle.putString("roomPrice", tvRoomPrice != null ? tvRoomPrice.getText().toString() : "0");
            bundle.putString("roomAddress", tvRoomAddress != null ? tvRoomAddress.getText().toString() : "Unknown");
            bundle.putString("bookedDate", tvBookedDate != null ? tvBookedDate.getText().toString() : "Unknown");
            bundle.putString("bookedSlot", tvBookedSlot != null ? tvBookedSlot.getText().toString() : "Unknown");
            bundle.putString("selectedRooms", tvSelectedRooms != null ? tvSelectedRooms.getText().toString() : "Unknown");
            bundle.putString("bookedPackage", tvBookedPackage != null ? tvBookedPackage.getText().toString() : "Unknown");

            Log.d(TAG, "Navigating to payment success with bundle: " + bundle.toString());
            
            // Navigate to payment success screen
            if (getView() != null) {
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.action_paymentFragment_to_paymentSuccessFragment, bundle);
            } else {
                Log.e(TAG, "View is null, cannot navigate");
                Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to payment success: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOrderId() {
        return "#" + System.currentTimeMillis();
    }
}