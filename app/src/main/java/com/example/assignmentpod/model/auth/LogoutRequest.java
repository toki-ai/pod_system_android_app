package com.example.assignmentpod.model.auth;

import com.google.gson.annotations.SerializedName;

public class LogoutRequest {
    @SerializedName("accessToken")
    private String accessToken;
    
    public LogoutRequest(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}