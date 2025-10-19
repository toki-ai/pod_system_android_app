package com.example.assignmentpod.data.remote.interceptor;

import android.content.Context;
import android.util.Log;

import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.remote.api.AuthAPI;
import com.example.assignmentpod.model.auth.RefreshTokenRequest;
import com.example.assignmentpod.model.auth.RefreshTokenResponse;
import com.example.assignmentpod.model.response.ApiResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Interceptor {
    private static final String TAG = "TokenAuthenticator";
    private final TokenManager tokenManager;
    private final String baseUrl;

    public TokenAuthenticator(Context context, String baseUrl) {
        this.tokenManager = new TokenManager(context);
        this.baseUrl = baseUrl;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Add access token to request if available
        String accessToken = tokenManager.getAccessToken();
        Request authenticatedRequest;
        
        if (accessToken != null) {
            authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        } else {
            authenticatedRequest = originalRequest;
        }

        Response response = chain.proceed(authenticatedRequest);

        if (response.code() == 401 && accessToken != null) {
            Log.d(TAG, "Received 401, attempting token refresh");
            
            synchronized (this) {
                String currentToken = tokenManager.getAccessToken();
                if (currentToken != null && currentToken.equals(accessToken)) {
                    if (refreshToken()) {
                        String newAccessToken = tokenManager.getAccessToken();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + newAccessToken)
                                .build();
                        
                        response.close();
                        return chain.proceed(newRequest);
                    } else {
                        tokenManager.clearTokens();
                        Log.e(TAG, "Token refresh failed, tokens cleared");
                    }
                }
            }
        }

        return response;
    }

    private boolean refreshToken() {
        try {
            String refreshToken = tokenManager.getRefreshToken();
            if (refreshToken == null) {
                Log.e(TAG, "No refresh token available");
                return false;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AuthAPI authAPI = retrofit.create(AuthAPI.class);
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
            
            Call<ApiResponse<RefreshTokenResponse>> call = authAPI.refreshToken(request);
            retrofit2.Response<ApiResponse<RefreshTokenResponse>> response = call.execute();

            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                RefreshTokenResponse refreshResponse = response.body().getData();
                if (refreshResponse != null) {
                    tokenManager.saveTokens(
                            refreshResponse.getAccessToken(),
                            refreshResponse.getRefreshToken(),
                            refreshResponse.getExpiryTimeMillis()
                    );
                    Log.d(TAG, "Token refreshed successfully");
                    return true;
                }
            }
            
            Log.e(TAG, "Token refresh failed with code: " + response.code());
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing token", e);
            return false;
        }
    }
}