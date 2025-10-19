package com.example.assignmentpod.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.data.remote.api.UserAPI;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.user.UserProfile;
import com.example.assignmentpod.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserAPI userAPI;
    private final TokenManager tokenManager;
    private final Context context;

    public UserRepository(Context context) {
        this.context = context;
        RetrofitClient.initialize(context);
        this.userAPI = RetrofitClient.getPublicInstance().create(UserAPI.class);
        this.tokenManager = new TokenManager(context);
    }

    public interface UserProfileCallback {
        void onSuccess(UserProfile userProfile);
        void onError(String error);
    }

    public void getUserProfile(UserProfileCallback callback) {
        Log.d(TAG, "Starting getUserProfile...");
        
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
        Call<ApiResponse<UserProfile>> call = userAPI.getUserProfile(authHeader);
        
        call.enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                Log.d(TAG, "API response received. Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserProfile> apiResponse = response.body();
                    Log.d(TAG, "Response body received. Success: " + apiResponse.isSuccess());
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        UserProfile profile = apiResponse.getData();
                        Log.d(TAG, "User profile loaded successfully. Name: " + profile.getName());
                        callback.onSuccess(profile);
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Failed to load user profile";
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
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Network error loading user profile: " + errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}