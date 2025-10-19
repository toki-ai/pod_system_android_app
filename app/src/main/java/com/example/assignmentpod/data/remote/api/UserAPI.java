package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.user.UserProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserAPI {
    @GET("/accounts/me")
    Call<ApiResponse<UserProfile>> getUserProfile(@Header("Authorization") String token);
}