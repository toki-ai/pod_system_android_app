package com.example.assignmentpod.model.auth;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    @SerializedName("accessToken")
    private String accessToken;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    // Default expiry time (1 hour) since backend doesn't provide expires_in
    private static final long DEFAULT_EXPIRES_IN = 3600; // 1 hour in seconds
    
    public RefreshTokenResponse() {}
    
    public RefreshTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public long getExpiresIn() {
        return DEFAULT_EXPIRES_IN;
    }
    
    public String getTokenType() {
        return "Bearer";
    }
    
    public long getExpiryTimeMillis() {
        return System.currentTimeMillis() + (DEFAULT_EXPIRES_IN * 1000);
    }
}