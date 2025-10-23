package com.example.assignmentpod.ui.tab.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.repository.OrderRepository;
import com.example.assignmentpod.model.order.Order;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.ui.adapter.OrderAdapter;
import com.example.assignmentpod.utils.DateTimeFormatterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderFragment extends Fragment implements OrderAdapter.OrderActionListener {
    private static final String TAG = "OrderFragment";

    // UI Components
    private RecyclerView recyclerViewOrders;
    private ProgressBar progressLoading;
    private ProgressBar progressLoadMore;
    private LinearLayout layoutEmptyState;
    private LinearLayout layoutErrorState;
    private TextView tvErrorMessage;
    private Button btnRetry;
    private Button btnBrowseRooms;
    private TextView tvTotalOrders;
    private Spinner spinnerStatusFilter;
    private Spinner spinnerSortBy;
    private Button btnClearFilters;

    // Data & State
    private OrderRepository orderRepository;
    private TokenManager tokenManager;
    private OrderAdapter orderAdapter;
    private List<Order> orders = new ArrayList<>();

    // Pagination state
    private int currentPage = 0;
    private static final int PAGE_SIZE = 5;
    private int totalPages = 1;
    private int totalRecords = 0;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    // Filter state
    private String selectedStatus = "Successfully";
    private String selectedSortBy = "newest";

    public OrderFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OrderFragment created");
        orderRepository = new OrderRepository(requireContext());
        tokenManager = new TokenManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "OrderFragment onCreateView");
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "OrderFragment onViewCreated");
        initializeUI(view);
        setupRecyclerView();
        setupFiltersAndSort();
        setupInfiniteScroll();
        loadOrderHistory();
    }

    private void initializeUI(View view) {
        recyclerViewOrders = view.findViewById(R.id.recycler_view_orders);
        progressLoading = view.findViewById(R.id.progress_loading);
        progressLoadMore = view.findViewById(R.id.progress_load_more);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        layoutErrorState = view.findViewById(R.id.layout_error_state);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRetry = view.findViewById(R.id.btn_retry);
        btnBrowseRooms = view.findViewById(R.id.btn_browse_rooms);
        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        spinnerStatusFilter = view.findViewById(R.id.spinner_status_filter);
        spinnerSortBy = view.findViewById(R.id.spinner_sort_by);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);

        btnRetry.setOnClickListener(v -> {
            orders.clear();
            currentPage = 0;
            loadOrderHistory();
        });
        btnBrowseRooms.setOnClickListener(v -> onBrowseRooms());
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter(orders, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewOrders.setLayoutManager(layoutManager);
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupInfiniteScroll() {
        recyclerViewOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = 
                        (LinearLayoutManager) recyclerView.getLayoutManager();
                
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Load more if user scrolled to near end and more pages available
                    if (!isLoading && hasMorePages &&
                            (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 3)) {
                        Log.d(TAG, "Loading more items... Current page: " + currentPage);
                        loadMoreOrders();
                    }
                }
            }
        });
    }

    private void setupFiltersAndSort() {
        // Status filter
        String[] statuses = {"Successfully", "Pending", "Confirmed", "Cancelled"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatusFilter.setAdapter(statusAdapter);
        spinnerStatusFilter.setSelection(0);
        spinnerStatusFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = statuses[position];
                resetAndReload();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Sort by
        String[] sortOptions = {"Newest First", "Oldest First", "Highest Amount", "Lowest Amount"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, sortOptions);
        spinnerSortBy.setAdapter(sortAdapter);
        spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedSortBy = position == 0 ? "newest" : position == 1 ? "oldest" : 
                                position == 2 ? "highest_amount" : "lowest_amount";
                sortOrders();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Clear filters button
        btnClearFilters.setOnClickListener(v -> {
            selectedStatus = "Successfully";
            selectedSortBy = "newest";
            spinnerStatusFilter.setSelection(0);
            spinnerSortBy.setSelection(0);
            resetAndReload();
        });
    }

    private void resetAndReload() {
        orders.clear();
        currentPage = 0;
        hasMorePages = true;
        loadOrderHistory();
    }

    private void setupPagination() {
        // Removed - pagination replaced with infinite scroll
    }

    private void loadOrderHistory() {
        showLoading();
        isLoading = true;

        String accountId = tokenManager.getAccountId();
        if (accountId == null || accountId.trim().isEmpty()) {
            showError("Account ID not found. Please login again.");
            isLoading = false;
            return;
        }

        orderRepository.getCustomerOrderHistory(accountId, currentPage, PAGE_SIZE, selectedStatus,
                new OrderRepository.OrderHistoryCallback() {
                    @Override
                    public void onSuccess(PaginationResponse<List<Order>> response) {
                        List<Order> newOrders = response.getData() != null ? response.getData() : new ArrayList<>();
                        currentPage = response.getCurrentPage();
                        totalPages = response.getTotalPage();
                        totalRecords = response.getTotalRecord();

                        // For first page load
                        if (currentPage == 0) {
                            orders.clear();
                        }

                        orders.addAll(newOrders);
                        hasMorePages = (currentPage + 1) < totalPages;

                        sortOrders();
                        updateUI();
                        hideLoading();
                        isLoading = false;
                    }

                    @Override
                    public void onError(String error) {
                        if (currentPage == 0) {
                            showError(error);
                        } else {
                            // Hide load more indicator on error
                            hideLoadMore();
                            Toast.makeText(requireContext(), "Error loading more: " + error, Toast.LENGTH_SHORT).show();
                        }
                        hideLoading();
                        isLoading = false;
                    }
                });
    }

    private void loadMoreOrders() {
        if (isLoading || !hasMorePages) {
            return;
        }

        isLoading = true;
        currentPage++;
        showLoadMore();

        String accountId = tokenManager.getAccountId();
        if (accountId == null || accountId.trim().isEmpty()) {
            hideLoadMore();
            isLoading = false;
            return;
        }

        orderRepository.getCustomerOrderHistory(accountId, currentPage, PAGE_SIZE, selectedStatus,
                new OrderRepository.OrderHistoryCallback() {
                    @Override
                    public void onSuccess(PaginationResponse<List<Order>> response) {
                        List<Order> newOrders = response.getData() != null ? response.getData() : new ArrayList<>();

                        if (!newOrders.isEmpty()) {
                            int insertPosition = orders.size();
                            orders.addAll(newOrders);
                            orderAdapter.notifyItemRangeInserted(insertPosition, newOrders.size());
                            Log.d(TAG, "Loaded " + newOrders.size() + " more orders");
                        }

                        totalPages = response.getTotalPage();
                        totalRecords = response.getTotalRecord();
                        hasMorePages = (currentPage + 1) < totalPages;

                        sortOrders();
                        hideLoadMore();
                        isLoading = false;
                    }

                    @Override
                    public void onError(String error) {
                        currentPage--; // Revert page number on error
                        hideLoadMore();
                        Toast.makeText(requireContext(), "Error loading more: " + error, Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    }
                });
    }

    private void sortOrders() {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        switch (selectedSortBy) {
            case "newest":
                Collections.sort(orders, (o1, o2) -> 
                        o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                break;
            case "oldest":
                Collections.sort(orders, (o1, o2) -> 
                        o1.getCreatedAt().compareTo(o2.getCreatedAt()));
                break;
            case "highest_amount":
                Collections.sort(orders, (o1, o2) -> 
                        Double.compare(o2.getFinalAmount() > 0 ? o2.getFinalAmount() : o2.getTotalAmount(),
                                o1.getFinalAmount() > 0 ? o1.getFinalAmount() : o1.getTotalAmount()));
                break;
            case "lowest_amount":
                Collections.sort(orders, (o1, o2) -> 
                        Double.compare(o1.getFinalAmount() > 0 ? o1.getFinalAmount() : o1.getTotalAmount(),
                                o2.getFinalAmount() > 0 ? o2.getFinalAmount() : o2.getTotalAmount()));
                break;
        }

        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }
    }

    private void updateUI() {
        tvTotalOrders.setText(totalRecords + " Orders");

        if (orders.isEmpty()) {
            showEmptyState();
        } else {
            showOrdersList();
        }
    }

    private void showLoading() {
        if (orders.isEmpty()) {
            progressLoading.setVisibility(View.VISIBLE);
            recyclerViewOrders.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
            layoutErrorState.setVisibility(View.GONE);
        }
    }

    private void showLoadMore() {
        progressLoadMore.setVisibility(View.VISIBLE);
    }

    private void hideLoadMore() {
        progressLoadMore.setVisibility(View.GONE);
    }

    private void showOrdersList() {
        recyclerViewOrders.setVisibility(View.VISIBLE);
        progressLoading.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        layoutErrorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerViewOrders.setVisibility(View.GONE);
        progressLoading.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
        layoutErrorState.setVisibility(View.GONE);
    }

    private void showError(String errorMessage) {
        recyclerViewOrders.setVisibility(View.GONE);
        progressLoading.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        layoutErrorState.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(errorMessage);
        Log.e(TAG, "Error: " + errorMessage);
    }

    private void hideLoading() {
        progressLoading.setVisibility(View.GONE);
    }

    @Override
    public void onViewInvoice(Order order, int position) {
        Toast.makeText(requireContext(), "View Invoice: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Implement invoice view (modal or PDF download)
    }

    @Override
    public void onReorder(Order order, int position) {
        Toast.makeText(requireContext(), "Reorder: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Implement reorder functionality
    }

    @Override
    public void onSupport(Order order, int position) {
        Toast.makeText(requireContext(), "Support: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // TODO: Implement support contact functionality
    }

    @Override
    public void onBrowseRooms() {
        Toast.makeText(requireContext(), "Browse Rooms", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to rooms browsing screen
    }
}