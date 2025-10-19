package com.example.assignmentpod.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import java.lang.ref.WeakReference;

public class LoadingManager {
    private WeakReference<Context> contextRef;
    private ProgressDialog progressDialog;
    
    public LoadingManager(Context context) {
        this.contextRef = new WeakReference<>(context);
    }
    
    public void showLoading(String message) {
        Context context = contextRef.get();
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message != null ? message : "Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    
    public void showLoading() {
        showLoading("Loading...");
    }
    
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                // Ignore if context is destroyed
            }
        }
    }
    
    public boolean isLoading() {
        return progressDialog != null && progressDialog.isShowing();
    }
}