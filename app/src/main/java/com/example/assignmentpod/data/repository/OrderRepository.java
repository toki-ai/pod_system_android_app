package com.example.assignmentpod.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.remote.api.OrderAPI;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.model.order.Order;
import com.example.assignmentpod.model.order.OrderDetail;
import com.example.assignmentpod.model.request.OrderDetailCreationRequest;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static final String TAG = "OrderRepository";
    private final OrderAPI orderAPI;
    private final TokenManager tokenManager;
    private final Context context;

    public OrderRepository(Context context) {
        this.context = context;
        RetrofitClient.initialize(context);
        this.orderAPI = RetrofitClient.getAuthenticatedInstance().create(OrderAPI.class);
        this.tokenManager = new TokenManager(context);
    }

    // Callback interfaces
    public interface OrderHistoryCallback {
        void onSuccess(PaginationResponse<List<Order>> response);
        void onError(String error);
    }

    public interface OrderDetailCallback {
        void onSuccess(ApiResponse<OrderDetail> response);
        void onError(String error);
    }

    public interface OrderDetailListCallback {
        void onSuccess(PaginationResponse<List<OrderDetail>> response);
        void onError(String error);
    }

    public interface CreateOrderCallback {
        void onSuccess(ApiResponse<String> response);
        void onError(String error);
    }

    /**
     * Get customer order history with pagination and filtering
     */
    public void getCustomerOrderHistory(String accountId, int page, int take, String status,
                                       OrderHistoryCallback callback) {
        Log.d(TAG, "Starting getCustomerOrderHistory for accountId: " + accountId + 
              ", page: " + page + ", take: " + take + ", status: " + status);

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.e(TAG, "No network available");
            callback.onError("No internet connection available");
            return;
        }

        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "No access token found");
            callback.onError("User not authenticated");
            return;
        }

        Call<PaginationResponse<List<Order>>> call = orderAPI.getCustomerOrderHistory(
                accountId, page, take, status
        );

        call.enqueue(new Callback<PaginationResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<Order>>> call, 
                                  Response<PaginationResponse<List<Order>>> response) {
                Log.d(TAG, "API response received. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PaginationResponse<List<Order>> paginationResponse = response.body();
                    Log.d(TAG, "Order history loaded successfully. Total: " + paginationResponse.getTotalRecord());
                    callback.onSuccess(paginationResponse);
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "HTTP error - Code: " + response.code() + ", Message: " + errorMessage);

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }

                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<List<Order>>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Network error loading order history: " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get detailed information for a specific order detail
     */
    public void getOrderDetail(String orderDetailId, OrderDetailCallback callback) {
        Log.d(TAG, "Starting getOrderDetail for orderDetailId: " + orderDetailId);

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.e(TAG, "No network available");
            callback.onError("No internet connection available");
            return;
        }

        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "No access token found");
            callback.onError("User not authenticated");
            return;
        }

        Call<ApiResponse<OrderDetail>> call = orderAPI.getOrderDetail(orderDetailId);

        call.enqueue(new Callback<ApiResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetail>> call, 
                                  Response<ApiResponse<OrderDetail>> response) {
                Log.d(TAG, "API response received. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderDetail> apiResponse = response.body();
                    Log.d(TAG, "Response body received. Success: " + apiResponse.isSuccess());

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        OrderDetail detail = apiResponse.getData();
                        Log.d(TAG, "Order detail loaded successfully. Room: " + detail.getRoomName());
                        callback.onSuccess(apiResponse);
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? 
                                apiResponse.getMessage() : "Failed to load order detail";
                        Log.e(TAG, "API returned error: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "HTTP error - Code: " + response.code() + ", Message: " + errorMessage);

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }

                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetail>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Network error loading order detail: " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Get order details by customer ID with pagination
     */
    public void getOrderDetailsByCustomer(String customerId, int page, int take, String status,
                                         OrderDetailListCallback callback) {
        Log.d(TAG, "Starting getOrderDetailsByCustomer for customerId: " + customerId + 
              ", page: " + page + ", take: " + take + ", status: " + status);

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.e(TAG, "No network available");
            callback.onError("No internet connection available");
            return;
        }

        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "No access token found");
            callback.onError("User not authenticated");
            return;
        }

        Call<PaginationResponse<List<OrderDetail>>> call = orderAPI.getOrderDetailsByCustomer(
                customerId, page, take, status
        );

        call.enqueue(new Callback<PaginationResponse<List<OrderDetail>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<OrderDetail>>> call, 
                                  Response<PaginationResponse<List<OrderDetail>>> response) {
                Log.d(TAG, "API response received. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    PaginationResponse<List<OrderDetail>> paginationResponse = response.body();
                    Log.d(TAG, "Order details loaded successfully. Total: " + paginationResponse.getTotalRecord());
                    callback.onSuccess(paginationResponse);
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "HTTP error - Code: " + response.code() + ", Message: " + errorMessage);

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }

                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<List<OrderDetail>>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Network error loading order details: " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Create a new order
     */
    public void createOrder(OrderDetailCreationRequest request, CreateOrderCallback callback) {
        Log.d(TAG, "Starting createOrder...");

        if (!NetworkUtils.isNetworkAvailable(context)) {
            Log.e(TAG, "No network available");
            callback.onError("No internet connection available");
            return;
        }

        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            Log.e(TAG, "No access token found");
            callback.onError("User not authenticated");
            return;
        }

        String authHeader = "Bearer " + accessToken;
        Call<ApiResponse<String>> call = orderAPI.createOrder(authHeader, request);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call,
                                  Response<ApiResponse<String>> response) {
                Log.d(TAG, "API response received. Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    Log.d(TAG, "Response body received. Success: " + apiResponse.isSuccess());
                    Log.d(TAG, "Order status: " + apiResponse.getData());
                    Log.d(TAG, "Message: " + apiResponse.getMessage());

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse);
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ?
                                apiResponse.getMessage() : "Failed to create order";
                        Log.e(TAG, "API returned error: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "HTTP error - Code: " + response.code() + ", Message: " + errorMessage);

                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }

                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Network error creating order: " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}
