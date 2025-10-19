package com.example.assignmentpod;

import android.app.Application;

import com.example.assignmentpod.data.remote.api.RetrofitClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize RetrofitClient with application context
        RetrofitClient.initialize(this);
    }
}