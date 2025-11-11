package com.example.assignmentpod.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.assignmentpod.MyApplication;
import com.example.assignmentpod.R;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.utils.LoadingManager;
import com.example.assignmentpod.utils.MultiStackNavigationManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private AuthRepository authRepository;
    private CartRepository cartRepository;
    private LoadingManager loadingManager;

    private Map<Integer, FragmentContainerView> navHostMap;
    private Map<Integer, NavController> navControllerMap;
    private MultiStackNavigationManager navManager;
    private int currentNavHostId = R.id.nav_host_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle system window insets (status bar, navigation bar)
        setupSystemWindowInsets();

        authRepository = new AuthRepository(this);
        cartRepository = CartRepository.getInstance(this);
        loadingManager = new LoadingManager(this);
        navManager = new MultiStackNavigationManager();
        navManager.restoreFromBundle(savedInstanceState);

        if (!RetrofitClient.isAuthenticated()) {
            navigateToLogin();
            return;
        }

        initNavigationComponents();
        setupBottomNavigation();
        setupBackPressedHandler();
        checkCartAndShowNotification();
        handlePaymentCallback();

        if (savedInstanceState == null) {
            showNavHost(R.id.nav_host_home);
            bottomNav.setSelectedItemId(R.id.nav_home);
            navManager.setCurrentTab(R.id.nav_host_home);
        } else {
            int restoredTab = navManager.getCurrentTab();
            if (restoredTab != 0) {
                showNavHost(restoredTab);
                bottomNav.setSelectedItemId(getMenuItemIdFromNavHost(restoredTab));
            }
        }
    }

    private void setupSystemWindowInsets() {
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Get system bars insets (status bar and navigation bar)
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            // Apply top padding to the nav host container to avoid status bar overlap
            View navHostContainer = findViewById(R.id.nav_host_container);
            if (navHostContainer != null) {
                navHostContainer.setPadding(
                    navHostContainer.getPaddingLeft(),
                    topInset,
                    navHostContainer.getPaddingRight(),
                    navHostContainer.getPaddingBottom()
                );
            }

            // Apply bottom padding to bottom navigation if needed
            View bottomNavigation = findViewById(R.id.bottom_navigation);
            if (bottomNavigation != null) {
                bottomNavigation.setPadding(
                    bottomNavigation.getPaddingLeft(),
                    bottomNavigation.getPaddingTop(),
                    bottomNavigation.getPaddingRight(),
                    Math.max(bottomInset - 16, 0) // Account for existing margin
                );
            }

            return insets;
        });
    }

    private void initNavigationComponents() {
        navHostMap = new HashMap<>();
        navControllerMap = new HashMap<>();

        // Lấy FragmentContainerView
        navHostMap.put(R.id.nav_host_home, findViewById(R.id.nav_host_home));
        navHostMap.put(R.id.nav_host_map, findViewById(R.id.nav_host_map));
        navHostMap.put(R.id.nav_host_chat, findViewById(R.id.nav_host_chat));
        navHostMap.put(R.id.nav_host_order, findViewById(R.id.nav_host_order));

        // Lấy NavController an toàn từ NavHostFragment
        navControllerMap.put(R.id.nav_host_home,
                ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_home))
                        .getNavController());
        navControllerMap.put(R.id.nav_host_map,
                ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_map))
                        .getNavController());
        navControllerMap.put(R.id.nav_host_chat,
                ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_chat))
                        .getNavController());
        navControllerMap.put(R.id.nav_host_order,
                ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_order))
                        .getNavController());
    }


    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int navHostId = getNavHostFromMenuId(item.getItemId());
            if (navHostId != 0) {
                showNavHost(navHostId);
                return true;
            }
            return false;
        });
    }

    private void showNavHost(int navHostId) {
        NavController currentNavController = navControllerMap.get(currentNavHostId);
        if (currentNavController != null) {
            navManager.saveNavigationState(currentNavHostId, currentNavController);
        }
        navHostMap.get(currentNavHostId).setVisibility(FragmentContainerView.GONE);
        navHostMap.get(navHostId).setVisibility(FragmentContainerView.VISIBLE);

        currentNavHostId = navHostId;
        navManager.setCurrentTab(navHostId);

        NavController targetNavController = navControllerMap.get(navHostId);
        if (targetNavController != null) {
            navManager.restoreNavigationState(navHostId, targetNavController);
        }
    }

    private int getNavHostFromMenuId(int itemId) {
        if (itemId == R.id.nav_home) return R.id.nav_host_home;
        if (itemId == R.id.nav_map) return R.id.nav_host_map;
        if (itemId == R.id.nav_chat) return R.id.nav_host_chat;
        if (itemId == R.id.nav_order) return R.id.nav_host_order;
        return 0;
    }

    private int getMenuItemIdFromNavHost(int navHostId) {
        if (navHostId == R.id.nav_host_home) return R.id.nav_home;
        if (navHostId == R.id.nav_host_map) return R.id.nav_map;
        if (navHostId == R.id.nav_host_chat) return R.id.nav_chat;
        if (navHostId == R.id.nav_host_order) return R.id.nav_order;
        return 0;
    }

    public NavController getCurrentNavController() {
        return navControllerMap.get(currentNavHostId);
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavController currentNavController = getCurrentNavController();
                        if (currentNavController == null || !currentNavController.popBackStack()) {
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        NavController currentNav = getCurrentNavController();
        if (currentNav != null) {
            navManager.saveNavigationState(currentNavHostId, currentNav);
        }
        navManager.saveToBundle(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (navManager != null) navManager.clearStates();
        super.onDestroy();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkCartAndShowNotification() {
        cartRepository.getCartItemCount().observe(this, count -> {
            if (count != null && count > 0) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CART_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_cart)
                        .setContentTitle("Giỏ hàng")
                        .setContentText("Bạn có " + count + " sản phẩm trong giỏ hàng")
                        .setAutoCancel(true);
                nm.notify(1, builder.build());
            }
        });
    }

    private void handlePaymentCallback() {
        // giữ nguyên code của Tri
    }
}
