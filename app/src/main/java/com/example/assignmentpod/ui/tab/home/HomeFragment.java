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
import com.example.assignmentpod.model.room.RoomType;
import com.example.assignmentpod.model.user.UserProfile;
import com.example.assignmentpod.ui.LoginActivity;
import com.example.assignmentpod.ui.adapter.RoomTypeAdapter;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment implements RoomTypeAdapter.OnRoomTypeClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeFragment";

    private HomeViewModel viewModel;

    private AuthRepository authRepository;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView ivFilter, ivCart;
    private TextView tvUserAvatar, tvBranchName;
    private RecyclerView rvRoomTypes;
    private ProgressBar progressBar;

    private RoomTypeAdapter roomTypeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        authRepository = new AuthRepository(requireContext());
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
        rvRoomTypes = view.findViewById(R.id.rv_room_types);
        progressBar = view.findViewById(R.id.progress_bar);
    }
    
    private void setupRecyclerView() {
        roomTypeAdapter = new RoomTypeAdapter();
        roomTypeAdapter.setOnRoomTypeClickListener(this);
        rvRoomTypes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRoomTypes.setAdapter(roomTypeAdapter);
    }
    
    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
    }
    
    private void observeViewModel() {
        viewModel.getRoomTypesLiveData().observe(getViewLifecycleOwner(), roomTypes -> {
            if (roomTypes != null) {
                roomTypeAdapter.setRoomTypes(roomTypes);
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
            Toast.makeText(getContext(), "Cart feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserAvatar(UserProfile userProfile) {
        if (userProfile != null) {
            String initials = userProfile.getInitials();
            tvUserAvatar.setText(initials);
        } else {
            tvUserAvatar.setText("U");
        }
    }    private void showUserMenu() {
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
                        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Logout failed: " + error, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBookClick(RoomType roomType) {
        Toast.makeText(getContext(), "Booking " + roomType.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(getContext(), "Filter demo - " + item.getTitle(), Toast.LENGTH_SHORT).show();
        
        return true;
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