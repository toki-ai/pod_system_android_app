package com.example.assignmentpod.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "token_prefs";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String TOKEN_EXPIRY_KEY = "token_expiry";
    private static final String ACCOUNT_ID_KEY = "account_id";
    
    private final SharedPreferences prefs;
    
    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveTokens(String accessToken, String refreshToken, long expiryTime) {
        prefs.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .putLong(TOKEN_EXPIRY_KEY, expiryTime)
            .apply();
    }
    
    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN_KEY, null);
    }
    
    public String getRefreshToken() {
        return prefs.getString(REFRESH_TOKEN_KEY, null);
    }
    
    public long getTokenExpiry() {
        return prefs.getLong(TOKEN_EXPIRY_KEY, 0);
    }
    
    public boolean isTokenExpired() {
        long expiry = getTokenExpiry();
        return expiry == 0 || System.currentTimeMillis() >= expiry;
    }
    
    public boolean hasValidTokens() {
        return getAccessToken() != null && getRefreshToken() != null && !isTokenExpired();
    }
    
    public void saveAccountId(String accountId) {
        prefs.edit()
            .putString(ACCOUNT_ID_KEY, accountId)
            .apply();
    }
    
    public String getAccountId() {
        return prefs.getString(ACCOUNT_ID_KEY, null);
    }
    
    public void clearTokens() {
        prefs.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(TOKEN_EXPIRY_KEY)
            .remove(ACCOUNT_ID_KEY)
            .apply();
    }
}