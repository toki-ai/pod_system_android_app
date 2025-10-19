package com.example.assignmentpod.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {
    
    /**
     * Check if device has internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null) {
            return false;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return false;
            }
            
            NetworkCapabilities networkCapabilities = 
                connectivityManager.getNetworkCapabilities(activeNetwork);
            
            return networkCapabilities != null && 
                   (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    
    /**
     * Get user friendly error message from HTTP status code
     */
    public static String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Bad request. Please check your input.";
            case 401:
                return "Authentication failed. Please login again.";
            case 403:
                return "Access forbidden. You don't have permission.";
            case 404:
                return "Resource not found.";
            case 408:
                return "Request timeout. Please try again.";
            case 429:
                return "Too many requests. Please wait and try again.";
            case 500:
                return "Server error. Please try again later.";
            case 502:
                return "Bad gateway. Please try again later.";
            case 503:
                return "Service unavailable. Please try again later.";
            case 504:
                return "Gateway timeout. Please try again later.";
            default:
                if (statusCode >= 400 && statusCode < 500) {
                    return "Client error. Please check your request.";
                } else if (statusCode >= 500) {
                    return "Server error. Please try again later.";
                } else {
                    return "Unknown error occurred.";
                }
        }
    }
    
    /**
     * Get user friendly error message from exception
     */
    public static String getErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error occurred.";
        }
        
        String message = throwable.getMessage();
        if (message == null || message.isEmpty()) {
            return "Network error occurred.";
        }
        
        // Common network error patterns
        if (message.contains("timeout")) {
            return "Connection timeout. Please check your internet connection.";
        } else if (message.contains("ConnectException") || message.contains("UnknownHostException")) {
            return "Cannot connect to server. Please check your internet connection.";
        } else if (message.contains("SocketTimeoutException")) {
            return "Request timeout. Please try again.";
        } else {
            return "Network error: " + message;
        }
    }
}