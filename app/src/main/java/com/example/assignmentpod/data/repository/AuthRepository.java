package com.example.assignmentpod.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.remote.api.AuthAPI;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.model.auth.AuthResponse;
import com.example.assignmentpod.model.auth.AuthenticationRequest;
import com.example.assignmentpod.model.auth.LogoutRequest;
import com.example.assignmentpod.model.request.AccountCreationRequest;
import com.example.assignmentpod.model.response.AccountResponse;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final AuthAPI authAPI;
    private final TokenManager tokenManager;
    private final Context context;
    
    public AuthRepository(Context context) {
        this.context = context;
        this.authAPI = RetrofitClient.getPublicInstance().create(AuthAPI.class);
        this.tokenManager = new TokenManager(context);
    }
    
    public interface LoginCallback {
        void onSuccess(AuthResponse authResponse);
        void onError(String error);
    }
    
    public interface LogoutCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface RegisterCallback {
        void onSuccess(AccountResponse accountResponse);
        void onError(String error);
    }

    public void login(String email, String password, LoginCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection available");
            return;
        }
        
        AuthenticationRequest loginRequest = new AuthenticationRequest(email, password);
        
        Call<ApiResponse<AuthResponse>> call = authAPI.login(loginRequest);
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse authResponse = apiResponse.getData();

                        tokenManager.saveTokens(
                                authResponse.getAccessToken(),
                                authResponse.getRefreshToken(),
                                authResponse.getExpiryTimeMillis()
                        );
                        
                        // Save account ID if available
                        if (authResponse.getAccount() != null && authResponse.getAccount().getId() != null) {
                            tokenManager.saveAccountId(authResponse.getAccount().getId());
                            Log.d(TAG, "Account ID saved: " + authResponse.getAccount().getId());
                        }
                        
                        Log.d(TAG, "Login successful for user: " + email);
                        callback.onSuccess(authResponse);
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Login failed";
                        Log.e(TAG, "Login failed: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "Login failed with code: " + response.code());
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Login network error", t);
                callback.onError(errorMessage);
            }
        });
    }

    public void logout(LogoutCallback callback) {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null) {
            tokenManager.clearTokens();
            RetrofitClient.clearInstances();
            callback.onSuccess();
            return;
        }
        
        LogoutRequest logoutRequest = new LogoutRequest(accessToken);
        Call<ApiResponse<Void>> call = authAPI.logout(logoutRequest);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                tokenManager.clearTokens();
                RetrofitClient.clearInstances();
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "Logout successful: " + response.body().getMessage());
                } else {
                    Log.w(TAG, "Server logout failed but local tokens cleared");
                }
                
                callback.onSuccess();
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                tokenManager.clearTokens();
                RetrofitClient.clearInstances();
                Log.w(TAG, "Network error during logout, but local tokens cleared", t);
                callback.onError("Network error during logout: " + t.getMessage());
            }
        });
    }

    public void register(String name, String email, String password, RegisterCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection available");
            return;
        }

        AccountCreationRequest registerRequest = new AccountCreationRequest(
                name, 
                email, 
                password, 
                0,
                "Customer",
                1
        );
        
        Call<ApiResponse<AccountResponse>> call = authAPI.register(registerRequest);
        call.enqueue(new Callback<ApiResponse<AccountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AccountResponse>> call, Response<ApiResponse<AccountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AccountResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d(TAG, "Registration successful for: " + email);
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Registration failed";
                        Log.e(TAG, "Registration failed: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "Registration failed with code: " + response.code());
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AccountResponse>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Registration network error", t);
                callback.onError(errorMessage);
            }
        });
    }

    public void register(String name, String email, String password, int buildingNumber, RegisterCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection available");
            return;
        }

        AccountCreationRequest registerRequest = new AccountCreationRequest(
                name, 
                email, 
                password, 
                buildingNumber, 
                "Customer",
                1
        );
        
        Call<ApiResponse<AccountResponse>> call = authAPI.register(registerRequest);
        call.enqueue(new Callback<ApiResponse<AccountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AccountResponse>> call, Response<ApiResponse<AccountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AccountResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d(TAG, "Registration successful for: " + email);
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        String errorMessage = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Registration failed";
                        Log.e(TAG, "Registration failed: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } else {
                    String errorMessage = NetworkUtils.getErrorMessage(response.code());
                    Log.e(TAG, "Registration failed with code: " + response.code());
                    callback.onError(errorMessage);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AccountResponse>> call, Throwable t) {
                String errorMessage = NetworkUtils.getErrorMessage(t);
                Log.e(TAG, "Registration network error", t);
                callback.onError(errorMessage);
            }
        });
    }

    public boolean isLoggedIn() {
        return tokenManager.hasValidTokens();
    }

    public String getAccessToken() {
        return tokenManager.getAccessToken();
    }

    public boolean isTokenExpired() {
        return tokenManager.isTokenExpired();
    }
}