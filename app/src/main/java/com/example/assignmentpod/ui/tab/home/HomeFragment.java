package com.example.assignmentpod.ui.tab.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.model.room.RoomType;
import com.example.assignmentpod.model.user.UserProfile;
import com.example.assignmentpod.ui.LoginActivity;
import com.example.assignmentpod.ui.adapter.RoomTypeAdapter;
import com.example.assignmentpod.utils.FilterHelper;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment implements RoomTypeAdapter.OnRoomTypeClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeFragment";
    private static final int PAGE_SIZE = 10;
    private static final int SCROLL_THRESHOLD = 3;

    private HomeViewModel viewModel;
    private CartRepository cartRepository;

    private AuthRepository authRepository;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivFilter, ivCart;
    private TextView tvUserAvatar, tvBranchName, tvCartBadge;
    private TextView tvEmptyState;
    private RecyclerView rvRoomTypes;
    private ProgressBar progressBar;

    private RoomTypeAdapter roomTypeAdapter;

    // Infinite Scroll State
    private int currentPage = 1;
    private boolean hasMorePages = true;
    private boolean isLoadingMore = false;
    private java.util.List<RoomType> allFetchedRoomTypes = new java.util.ArrayList<>();

    // Filter State
    private String filterRoomTypeName = null;
    private Double filterMinPrice = null;
    private Double filterMaxPrice = null;
    private Integer filterCapacity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        authRepository = new AuthRepository(requireContext());
        cartRepository = CartRepository.getInstance(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initViews(view);
            setupRecyclerView();
            setupClickListeners();
            setupNavigationDrawer();
            observeViewModel();
            observeCartCount();

            viewModel.refreshData();
            
            Log.d(TAG, "HomeFragment setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
        }
    }
    
    private void initViews(View view) {
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        ivFilter = view.findViewById(R.id.iv_filter);
        ivCart = view.findViewById(R.id.iv_cart);
        tvUserAvatar = view.findViewById(R.id.tv_user_avatar);
        tvBranchName = view.findViewById(R.id.tv_branch_name);
        tvCartBadge = view.findViewById(R.id.tv_cart_badge);
        rvRoomTypes = view.findViewById(R.id.rv_room_types);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);
    }
    
    private void setupRecyclerView() {
        roomTypeAdapter = new RoomTypeAdapter();
        roomTypeAdapter.setOnRoomTypeClickListener(this);
        rvRoomTypes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRoomTypes.setAdapter(roomTypeAdapter);
        setupInfiniteScroll();
    }

    private void setupInfiniteScroll() {
        rvRoomTypes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    // If user scrolled to within SCROLL_THRESHOLD items of the end, load more
                    if (!isLoadingMore && hasMorePages 
                            && lastVisibleItemPosition >= (totalItemCount - SCROLL_THRESHOLD)) {
                        loadMoreRoomTypes();
                    }
                }
            }
        });
    }

    private void loadMoreRoomTypes() {
        if (isLoadingMore || !hasMorePages) {
            return;
        }

        isLoadingMore = true;
        currentPage++;
        Log.d(TAG, "Loading page " + currentPage);

        viewModel.loadMoreRoomTypes(currentPage, PAGE_SIZE, new HomeViewModel.LoadMoreCallback() {
            @Override
            public void onSuccess(PaginationResponse<java.util.List<RoomType>> response, java.util.List<RoomType> newRoomTypes) {
                isLoadingMore = false;

                if (response != null) {
                    hasMorePages = currentPage < response.getTotalPage();
                    allFetchedRoomTypes.addAll(newRoomTypes);
                    
                    // Apply current filters and update adapter
                    applyFiltersAndUpdateUI();
                    
                    Log.d(TAG, "Loaded page " + currentPage + ", hasMorePages: " + hasMorePages);
                } else {
                    hasMorePages = false;
                }
            }

            @Override
            public void onError(String error) {
                isLoadingMore = false;
                hasMorePages = false;
                Toast.makeText(getContext(), "Failed to load more: " + error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading more: " + error);
            }
        });
    }
    
    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
    }
    
    private void observeViewModel() {
        viewModel.getRoomTypesLiveData().observe(getViewLifecycleOwner(), roomTypes -> {
            if (roomTypes != null) {
                // Clear previous state and reset pagination
                allFetchedRoomTypes.clear();
                allFetchedRoomTypes.addAll(roomTypes);
                currentPage = 1;
                hasMorePages = true;
                isLoadingMore = false;
                
                // Apply filters and update UI
                applyFiltersAndUpdateUI();
                Log.d(TAG, "Initial room types loaded: " + roomTypes.size());
            }
        });

        viewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), userProfile -> {
            Log.d(TAG, "User profile observed: " + (userProfile != null ? userProfile.getName() : "null"));
            if (userProfile != null) {
                updateUserAvatar(userProfile);
            } else {
                tvUserAvatar.setText("U");
            }
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            showLoading(isLoading != null && isLoading);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyFiltersAndUpdateUI() {
        // Apply filters to all fetched room types
        java.util.List<RoomType> filteredRoomTypes = FilterHelper.filterRoomTypes(
                allFetchedRoomTypes,
                filterRoomTypeName,
                filterMinPrice,
                filterMaxPrice,
                null,
                filterCapacity
        );

        // Update RecyclerView with filtered data
        roomTypeAdapter.setRoomTypes(filteredRoomTypes);
        
        // Show/hide empty state
        if (filteredRoomTypes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvRoomTypes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvRoomTypes.setVisibility(View.VISIBLE);
        }
        
        Log.d(TAG, "Applied filters. Result: " + filteredRoomTypes.size() + " room types from " + allFetchedRoomTypes.size());
    }
    
    private void setupClickListeners() {
        ivFilter.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        tvUserAvatar.setOnClickListener(v -> showUserMenu());

        ivCart.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_homeFragment_to_cartFragment);
        });
    }

    private void updateUserAvatar(UserProfile userProfile) {
        if (userProfile != null) {
            String initials = userProfile.getInitials();
            tvUserAvatar.setText(initials);
        } else {
            tvUserAvatar.setText("U");
        }
    }
    
    private void observeCartCount() {
        cartRepository.getCartItemCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(String.valueOf(count));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        });
    }
    
    private void showUserMenu() {
        PopupMenu popup = new PopupMenu(getContext(), tvUserAvatar);
        popup.getMenuInflater().inflate(R.menu.user_popup_menu, popup.getMenu());

        UserProfile currentUser = viewModel.getUserProfileLiveData().getValue();
        if (currentUser != null) {
            popup.getMenu().findItem(R.id.menu_user_name).setTitle(currentUser.getName());
        }
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_logout) {
                performLogout();
                return true;
            }
            return false;
        });
        
        popup.show();
    }
    
    private void performLogout() {
        authRepository.logout(new AuthRepository.LogoutCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đăng xuất thất bại: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRoomTypeClick(RoomType roomType) {
        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putString("roomTypeName", roomType.getName());
            args.putInt("roomTypePrice", (int) roomType.getPrice());
            args.putInt("roomTypeId", roomType.getId());
            navController.navigate(R.id.action_homeFragment_to_roomTypeDetailFragment, args);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to room type detail", e);
            Toast.makeText(getContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookClick(RoomType roomType) {
        Toast.makeText(getContext(), "Đang đặt: " + roomType.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCartClick(RoomType roomType) {
        // Build a minimal Room object to satisfy repository API
        com.example.assignmentpod.model.room.Room room = new com.example.assignmentpod.model.room.Room();
        room.setId(roomType.getId());
        room.setName(roomType.getName());
        room.setDescription("Room for booking");
        room.setImage("");
        room.setRoomType(roomType);

        cartRepository.addToCart(room);
        Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_filter_single) {
            filterRoomTypeName = "Single Pod";
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Filtered: Single Pod", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_filter_double) {
            filterRoomTypeName = "Double Pod";
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Filtered: Double Pod", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_filter_meeting) {
            filterRoomTypeName = "Meeting Room";
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Filtered: Meeting Room", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_filter_conference) {
            filterRoomTypeName = "Conference Room";
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Filtered: Conference Room", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_price_all) {
            filterMinPrice = null;
            filterMaxPrice = null;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "All Prices", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_price_low) {
            filterMinPrice = 0.0;
            filterMaxPrice = 50000.0;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Under 50.000 VND", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_price_medium) {
            filterMinPrice = 50000.0;
            filterMaxPrice = 100000.0;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "50.000 - 100.000 VND", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_price_high) {
            filterMinPrice = 100000.0;
            filterMaxPrice = null;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "Above 100.000 VND", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_capacity_all) {
            filterCapacity = null;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "All Capacity", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_capacity_1) {
            filterCapacity = 1;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "1 Person", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_capacity_2) {
            filterCapacity = 2;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "2 People", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_capacity_3) {
            filterCapacity = 3;
            applyFiltersAndUpdateUI();
            Toast.makeText(getContext(), "3+ People", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_reset_filters) {
            clearFilters();
            Toast.makeText(getContext(), "All filters cleared", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.nav_cart) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_homeFragment_to_cartFragment);
            return true;
        }

        Toast.makeText(getContext(), "Bộ lọc demo - " + item.getTitle(), Toast.LENGTH_SHORT).show();
        
        return true;
    }

    private void applyFilterByName(String filterName) {
        // This method is kept for backward compatibility but can be enhanced
        // Reset current filters
        filterRoomTypeName = null;
        filterMinPrice = null;
        filterMaxPrice = null;
        filterCapacity = null;

        // Apply specific filter based on selection
        if (filterName != null && !filterName.isEmpty()) {
            // Check if it's a room type name
            java.util.List<String> roomTypeNames = FilterHelper.extractUniqueRoomNames(allFetchedRoomTypes);
            if (roomTypeNames.contains(filterName)) {
                filterRoomTypeName = filterName;
            }
        }

        // Reset pagination and apply filters
        currentPage = 1;
        hasMorePages = true;
        applyFiltersAndUpdateUI();
    }

    public void clearFilters() {
        filterRoomTypeName = null;
        filterMinPrice = null;
        filterMaxPrice = null;
        filterCapacity = null;
        currentPage = 1;
        hasMorePages = true;
        applyFiltersAndUpdateUI();
    }

    public void applyFiltersByRange(double minPrice, double maxPrice) {
        filterMinPrice = minPrice;
        filterMaxPrice = maxPrice;
        currentPage = 1;
        hasMorePages = true;
        applyFiltersAndUpdateUI();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "HomeFragment onDestroyView");
        super.onDestroyView();
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "HomeFragment onDestroy");
        super.onDestroy();
    }
}