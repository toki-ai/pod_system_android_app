package com.example.assignmentpod.utils;

public class Constants {
    
    // API Configuration
    public static final String BASE_URL = "http://10.0.2.2:8080/"; // Use 10.0.2.2 for Android emulator
    public static final int CONNECTION_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
    
    // Token Configuration
    public static final long TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000; // 5 minutes in milliseconds
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    
    // SharedPreferences Keys
    public static final String PREF_TOKEN = "token_prefs";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_TOKEN_EXPIRY = "token_expiry";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    
    // Request Codes
    public static final int REQUEST_LOGIN = 1001;
    public static final int REQUEST_PERMISSIONS = 1002;
    
    // Error Messages
    public static final String ERROR_NETWORK_UNAVAILABLE = "No internet connection available";
    public static final String ERROR_TOKEN_EXPIRED = "Session expired. Please login again.";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access. Please login again.";
    public static final String ERROR_SERVER_ERROR = "Server error. Please try again later.";
    public static final String ERROR_UNKNOWN = "An unknown error occurred";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE = 1;
    
    // API Endpoints
    public static final String ENDPOINT_LOGIN = "auth/login";
    public static final String ENDPOINT_REFRESH = "auth/refresh-token";
    public static final String ENDPOINT_LOGOUT = "auth/logout";
    public static final String ENDPOINT_ROOMS = "rooms";
    public static final String ENDPOINT_PUBLIC_ROOMS = "public/rooms";
    public static final String ENDPOINT_REGISTER = "auth/register";
    public static final String ENDPOINT_FORGOT_PASSWORD = "auth/forgot-password";
    
    // HTTP Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    
    // Content Types
    public static final String CONTENT_TYPE_JSON = "application/json";

    // MoMo constants
    public static final String MOMO_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    public static final String MOMO_PARTNER_CODE = "MOMO";
    public static final String MOMO_ACCESS_KEY = "F8BBA842ECF85";
    public static final String MOMO_SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    public static final String MOMO_REDIRECT_URL = "demozpdk://app";
    public static final String MOMO_IPN_URL = "https://webhook.site/123abc";

    // ZaloPay constants
    public static final String ZALOPAY_ENDPOINT = "https://sb-openapi.zalopay.vn/v2/create";
    public static final String ZALOPAY_APP_ID = "2553";
    public static final String ZALOPAY_KEY1 = "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL";
    public static final String ZALOPAY_REDIRECT_URL = "demozpdk://app";
    private Constants() {
        // Private constructor to prevent instantiation
    }
}