package com.example.assignmentpod.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.MyApplication;
import com.example.assignmentpod.R;
import com.example.assignmentpod.data.local.database.AppDatabase;
import com.example.assignmentpod.data.local.database.RoomDAO;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.utils.LoadingManager;
import com.example.assignmentpod.utils.MultiStackNavigationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNav;
    private AuthRepository authRepository;
    private CartRepository cartRepository;
    private LoadingManager loadingManager;
    
    // Navigation components
    private Map<Integer, FragmentContainerView> navHostMap;
    private Map<Integer, NavController> navControllerMap;
    private MultiStackNavigationManager navManager;
    private int currentNavHostId = R.id.nav_host_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_main);

            authRepository = new AuthRepository(this);
            cartRepository = CartRepository.getInstance(this);
            loadingManager = new LoadingManager(this);
            navManager = new MultiStackNavigationManager();
            
            // Restore navigation state if available
            navManager.restoreFromBundle(savedInstanceState);
            
            // Check if user is authenticated
            if (!RetrofitClient.isAuthenticated()) {
                navigateToLogin();
                return;
            }

            initNavigationComponents();
            setupBottomNavigation();
            setupBackPressedHandler();
            checkCartAndShowNotification();
            
            // Set default tab if this is a fresh start
            if (savedInstanceState == null) {
                showNavHost(R.id.nav_host_home);
                bottomNav.setSelectedItemId(R.id.nav_home);
                navManager.setCurrentTab(R.id.nav_host_home);
            } else {
                // Restore previous tab state
                int restoredTab = navManager.getCurrentTab();
                if (restoredTab != 0) {
                    showNavHost(restoredTab);
                    // Set bottom nav selection based on restored tab
                    int menuItemId = getMenuItemIdFromNavHost(restoredTab);
                    if (menuItemId != 0) {
                        bottomNav.setSelectedItemId(menuItemId);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo ứng dụng", Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initNavigationComponents() {
        // Initialize navigation host containers
        navHostMap = new HashMap<>();
        navControllerMap = new HashMap<>();
        
        navHostMap.put(R.id.nav_host_home, findViewById(R.id.nav_host_home));
        navHostMap.put(R.id.nav_host_map, findViewById(R.id.nav_host_map));
        navHostMap.put(R.id.nav_host_chat, findViewById(R.id.nav_host_chat));
        navHostMap.put(R.id.nav_host_order, findViewById(R.id.nav_host_order));
        
        // Initialize navigation controllers
        try {
            navControllerMap.put(R.id.nav_host_home, Navigation.findNavController(this, R.id.nav_host_home));
            navControllerMap.put(R.id.nav_host_map, Navigation.findNavController(this, R.id.nav_host_map));
            navControllerMap.put(R.id.nav_host_chat, Navigation.findNavController(this, R.id.nav_host_chat));
            navControllerMap.put(R.id.nav_host_order, Navigation.findNavController(this, R.id.nav_host_order));
        } catch (Exception e) {
            Log.e(TAG, "Error initializing navigation controllers", e);
        }
    }
    
    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            try {
                int itemId = item.getItemId();
                int targetNavHostId;

                if (itemId == R.id.nav_home) {
                    targetNavHostId = R.id.nav_host_home;
                } else if (itemId == R.id.nav_map) {
                    targetNavHostId = R.id.nav_host_map;
                } else if (itemId == R.id.nav_chat) {
                    targetNavHostId = R.id.nav_host_chat;
                } else if (itemId == R.id.nav_order) {
                    targetNavHostId = R.id.nav_host_order;
                } else {
                    return false;
                }

                showNavHost(targetNavHostId);
                return true;
                
            } catch (Exception e) {
                Log.e(TAG, "Error switching tabs", e);
                Toast.makeText(this, "Lỗi tải tab", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    
    private void showNavHost(int navHostId) {
        // Save current navigation state before switching
        NavController currentNavController = navControllerMap.get(currentNavHostId);
        if (currentNavController != null) {
            navManager.saveNavigationState(currentNavHostId, currentNavController);
        }
        
        // Hide current nav host
        if (navHostMap.get(currentNavHostId) != null) {
            navHostMap.get(currentNavHostId).setVisibility(View.GONE);
        }
        
        // Show target nav host
        if (navHostMap.get(navHostId) != null) {
            navHostMap.get(navHostId).setVisibility(View.VISIBLE);
            currentNavHostId = navHostId;
            navManager.setCurrentTab(navHostId);
            
            // Restore navigation state for the new tab
            NavController targetNavController = navControllerMap.get(navHostId);
            if (targetNavController != null) {
                navManager.restoreNavigationState(navHostId, targetNavController);
            }
        }
        
        Log.d(TAG, "Switched to nav host: " + navHostId);
    }
    
    private int getMenuItemIdFromNavHost(int navHostId) {
        if (navHostId == R.id.nav_host_home) {
            return R.id.nav_home;
        } else if (navHostId == R.id.nav_host_map) {
            return R.id.nav_map;
        } else if (navHostId == R.id.nav_host_chat) {
            return R.id.nav_chat;
        } else if (navHostId == R.id.nav_host_order) {
            return R.id.nav_order;
        } else {
            return 0;
        }
    }
    
    public NavController getCurrentNavController() {
        return navControllerMap.get(currentNavHostId);
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
        NavController currentNavController = getCurrentNavController();
        if (currentNavController != null) {
            return currentNavController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
    
    private void setupBackPressedHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavController currentNavController = getCurrentNavController();
                if (currentNavController != null && !currentNavController.popBackStack()) {
                    // If no fragments to pop in current tab, handle normally
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current navigation state
        NavController currentNavController = getCurrentNavController();
        if (currentNavController != null) {
            navManager.saveNavigationState(currentNavHostId, currentNavController);
        }
        navManager.saveToBundle(outState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (navManager != null) {
            navManager.clearStates();
        }
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
}