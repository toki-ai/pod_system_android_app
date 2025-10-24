package com.example.assignmentpod.utils;

import android.graphics.Color;

/**
 * Utility class for mapping and formatting order status
 */
public class OrderStatusMapper {
    
    /**
     * Map API status to user-friendly display text
     * @param apiStatus The API status enum value
     * @return User-friendly status text
     */
    public static String getDisplayStatus(String apiStatus) {
        if (apiStatus == null) {
            return "Unknown";
        }
        
        switch (apiStatus.toLowerCase()) {
            case "successfully":
                return "Completed";
            case "pending":
                return "Pending";
            case "confirmed":
                return "Confirmed";
            case "cancelled":
                return "Cancelled";
            case "completed":
                return "Completed";
            default:
                return apiStatus;
        }
    }

    /**
     * Get color code for status badge
     * @param apiStatus The API status enum value
     * @return Color integer
     */
    public static int getStatusColor(String apiStatus) {
        if (apiStatus == null) {
            return Color.GRAY;
        }
        
        switch (apiStatus.toLowerCase()) {
            case "successfully":
            case "completed":
                return Color.parseColor("#4CAF50"); // Green
            case "pending":
                return Color.parseColor("#FFC107"); // Amber/Orange
            case "confirmed":
                return Color.parseColor("#2196F3"); // Blue
            case "cancelled":
                return Color.parseColor("#F44336"); // Red
            default:
                return Color.GRAY;
        }
    }

    /**
     * Get background resource ID for status badge
     * @param apiStatus The API status enum value
     * @return Resource ID of the drawable
     */
    public static int getStatusBackgroundResource(String apiStatus) {
        if (apiStatus == null) {
            return android.R.color.darker_gray;
        }
        
        switch (apiStatus.toLowerCase()) {
            case "successfully":
            case "completed":
                return android.R.color.holo_green_light;
            case "pending":
                return android.R.color.holo_orange_light;
            case "confirmed":
                return android.R.color.holo_blue_light;
            case "cancelled":
                return android.R.color.holo_red_light;
            default:
                return android.R.color.darker_gray;
        }
    }

    /**
     * Check if order can be cancelled
     * @param apiStatus The API status enum value
     * @return True if order can be cancelled
     */
    public static boolean canBeCancelled(String apiStatus) {
        if (apiStatus == null) {
            return false;
        }
        return apiStatus.toLowerCase().equals("pending");
    }

    /**
     * Check if order can be reordered
     * @param apiStatus The API status enum value
     * @return True if order can be reordered
     */
    public static boolean canBeReordered(String apiStatus) {
        if (apiStatus == null) {
            return false;
        }
        String status = apiStatus.toLowerCase();
        return status.equals("successfully") || status.equals("completed");
    }

    /**
     * Get status description for details view
     * @param apiStatus The API status enum value
     * @return Status description
     */
    public static String getStatusDescription(String apiStatus) {
        if (apiStatus == null) {
            return "Status unknown";
        }
        
        switch (apiStatus.toLowerCase()) {
            case "successfully":
            case "completed":
                return "Order completed successfully";
            case "pending":
                return "Awaiting confirmation";
            case "confirmed":
                return "Order confirmed";
            case "cancelled":
                return "Order has been cancelled";
            default:
                return apiStatus;
        }
    }
}
