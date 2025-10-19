package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.auth.AuthResponse;
import com.example.assignmentpod.model.auth.AuthenticationRequest;
import com.example.assignmentpod.model.auth.LogoutRequest;
import com.example.assignmentpod.model.auth.RefreshTokenRequest;
import com.example.assignmentpod.model.auth.RefreshTokenResponse;
import com.example.assignmentpod.model.request.AccountCreationRequest;
import com.example.assignmentpod.model.response.AccountResponse;
import com.example.assignmentpod.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body AuthenticationRequest loginRequest);
    
    @POST("accounts")
    Call<ApiResponse<AccountResponse>> register(@Body AccountCreationRequest registerRequest);
    
    @POST("auth/refresh-token")
    Call<ApiResponse<RefreshTokenResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
    
    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Body LogoutRequest logoutRequest);
}