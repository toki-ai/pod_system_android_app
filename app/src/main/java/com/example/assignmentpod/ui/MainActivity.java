package com.example.assignmentpod.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.MyApplication;
import com.example.assignmentpod.R;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.utils.LoadingManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNav;
    private AuthRepository authRepository;
    private CartRepository cartRepository;
    private LoadingManager loadingManager;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            authRepository = new AuthRepository(this);
            cartRepository = CartRepository.getInstance(this);
            loadingManager = new LoadingManager(this);

            // Check if user is authenticated
            if (!RetrofitClient.isAuthenticated()) {
                navigateToLogin();
                return;
            }

            initNavigationComponents();
            setupBottomNavigation();
            setupBackPressedHandler();
            checkCartAndShowNotification();
            // Handle deep link after layout is fully loaded
            handlePaymentCallback();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo ứng dụng", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensure deep link is handled after the activity is fully started
        handlePaymentCallback();
    }

    private void handlePaymentCallback() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            String scheme = data.getScheme();
            String host = data.getHost();

            Log.d(TAG, "Received deep link: " + data.toString());

            if ("demozpdk".equals(scheme) && "app".equals(host)) {
                if (navController == null) {
                    Log.w(TAG, "NavController is null, reinitializing...");
                    initNavigationComponents();
                }

                if (navController != null) {
                    try {
                        // Log current destination for debugging
                        Log.d(TAG, "Current destination: " + (navController.getCurrentDestination() != null
                                ? navController.getCurrentDestination().getId() : "null"));

                        // Clear back stack up to homeFragment
                        navController.popBackStack(R.id.homeFragment, false);

                        // Retrieve payment data from SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("PaymentData", MODE_PRIVATE);
                        Bundle bundle = new Bundle();
                        bundle.putString("orderId", prefs.getString("orderId", "#" + System.currentTimeMillis()));
                        bundle.putString("customerName", prefs.getString("customerName", "Khách hàng"));
                        bundle.putString("totalAmount", prefs.getString("totalAmount", "1.374.000 VND"));
                        bundle.putString("roomName", prefs.getString("roomName", "Phòng POD đôi"));
                        bundle.putString("roomPrice", prefs.getString("roomPrice", "20.000 VND/tiếng"));
                        bundle.putString("roomAddress", prefs.getString("roomAddress", "Đỗm biết"));
                        bundle.putString("bookedDate", prefs.getString("bookedDate", "24/01/2024"));
                        bundle.putString("bookedSlot", prefs.getString("bookedSlot", "7h-9h, 9h-11h"));
                        bundle.putString("selectedRooms", prefs.getString("selectedRooms", "Phòng 101, Phòng 102"));
                        bundle.putString("bookedPackage", prefs.getString("bookedPackage", "Gói tuần"));

                        // Navigate to paymentSuccessFragment using global action
                        navController.navigate(R.id.action_global_to_paymentSuccessFragment, bundle);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to PaymentSuccessFragment: " + e.getMessage(), e);
                        Toast.makeText(this, "Lỗi điều hướng sau thanh toán", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "NavController is still null, cannot navigate");
                    Toast.makeText(this, "Lỗi hệ thống: Không thể điều hướng", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initNavigationComponents() {
        try {
            navController = Navigation.findNavController(this, R.id.nav_host_home);
            Log.d(TAG, "NavController initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing navigation controller", e);
            navController = null;
        }
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            try {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    navController.navigate(R.id.homeFragment);
                } else if (itemId == R.id.nav_map) {
                    Toast.makeText(this, "Map tab not implemented", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_chat) {
                    Toast.makeText(this, "Chat tab not implemented", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_order) {
                    Toast.makeText(this, "Order tab not implemented", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Error switching tabs", e);
                Toast.makeText(this, "Lỗi tải tab", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        loadingManager.showLoading("Đang đăng xuất...");

        authRepository.logout(new AuthRepository.LogoutCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    loadingManager.hideLoading();
                    Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    loadingManager.hideLoading();
                    Toast.makeText(MainActivity.this, "Lỗi đăng xuất: " + error, Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                });
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    private void setupBackPressedHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController != null && !navController.popBackStack()) {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void checkCartAndShowNotification() {
        cartRepository.getCartItemCount().observe(this, count -> {
            if (count != null && count > 0) {
                showCartNotification(count);
            }
        });
    }

    private void showCartNotification(int itemCount) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CART_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cart)
                .setContentTitle("Giỏ hàng")
                .setContentText("Bạn có " + itemCount + " sản phẩm trong giỏ hàng")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlePaymentCallback();
    }
}