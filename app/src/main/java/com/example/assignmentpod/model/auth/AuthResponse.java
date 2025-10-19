package com.example.assignmentpod.model.auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    private String accessToken;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("account")
    private AccountResponse account;
    
    // Default expiry time (1 hour) since backend doesn't provide expires_in
    private static final long DEFAULT_EXPIRES_IN = 3600; // 1 hour in seconds
    
    public AuthResponse() {}
    
    public AuthResponse(String accessToken, String refreshToken, AccountResponse account) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.account = account;
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
    
    public AccountResponse getAccount() {
        return account;
    }
    
    public void setAccount(AccountResponse account) {
        this.account = account;
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
    
    public static class AccountResponse {
        @SerializedName("id")
        private String id;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("avatar")
        private String avatar;
        
        // Getters and setters
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getAvatar() {
            return avatar;
        }
        
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}