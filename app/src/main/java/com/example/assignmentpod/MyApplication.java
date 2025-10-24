package com.example.assignmentpod;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.assignmentpod.data.local.database.AppDatabase;
import com.example.assignmentpod.data.remote.api.RetrofitClient;

public class MyApplication extends Application {
    public static final String CART_CHANNEL_ID = "cart_channel";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize RetrofitClient with application context
        RetrofitClient.initialize(this);
        
        // Initialize Room database
        AppDatabase.getDatabase(this);
        
        // Create notification channel for cart notifications
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel cartChannel = new NotificationChannel(
                CART_CHANNEL_ID,
                "Cart Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            cartChannel.setDescription("Notifications for cart items");
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(cartChannel);
        }
    }
}