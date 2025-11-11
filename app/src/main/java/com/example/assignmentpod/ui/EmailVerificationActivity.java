package com.example.assignmentpod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentpod.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {
    private static final String TAG = "EmailVerification";
    
    private FirebaseAuth mAuth;
    private MaterialButton btnResendVerification;
    private MaterialButton btnCheckVerification;
    private MaterialButton btnBackToLogin;
    
    private Handler verificationCheckHandler;
    private Runnable verificationCheckRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        
        mAuth = FirebaseAuth.getInstance();
        
        initViews();
        setupClickListeners();
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            navigateToMain();
            return;
        }
        
        startAutoVerificationCheck();
    }
    
    private void initViews() {
        btnResendVerification = findViewById(R.id.btn_resend_verification);
        btnCheckVerification = findViewById(R.id.btn_check_verification);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        
        // Display user email
        TextView tvUserEmail = findViewById(R.id.tv_user_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            tvUserEmail.setText(currentUser.getEmail());
        }
    }
    
    private void setupClickListeners() {
        btnResendVerification.setOnClickListener(v -> resendVerificationEmail());
        btnCheckVerification.setOnClickListener(v -> checkEmailVerification());
        btnBackToLogin.setOnClickListener(v -> backToLogin());
    }
    
    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.isEmailVerified()) {
            btnResendVerification.setEnabled(false);
            btnResendVerification.setText("Đang gửi...");
            
            user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    btnResendVerification.setEnabled(true);
                    btnResendVerification.setText("Gửi lại email xác thực");
                    
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email xác thực đã được gửi lại", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Verification email sent to: " + user.getEmail());
                    } else {
                        Log.e(TAG, "Failed to send verification email", task.getException());
                        Toast.makeText(this, "Lỗi gửi email: " + task.getException().getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
    
    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            btnCheckVerification.setEnabled(false);
            btnCheckVerification.setText("Đang kiểm tra...");
            
            user.reload().addOnCompleteListener(task -> {
                btnCheckVerification.setEnabled(true);
                btnCheckVerification.setText("Kiểm tra xác thực");
                
                if (task.isSuccessful()) {
                    user.reload();
                    if (user.isEmailVerified()) {
                        Toast.makeText(this, "Email đã được xác thực thành công!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(this, "Email chưa được xác thực. Vui lòng kiểm tra hộp thư.", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to reload user", task.getException());
                    Toast.makeText(this, "Lỗi kiểm tra: " + task.getException().getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void startAutoVerificationCheck() {
        verificationCheckHandler = new Handler(Looper.getMainLooper());
        verificationCheckRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && user.isEmailVerified()) {
                            navigateToMain();
                        } else {
                            // Check again after 3 seconds
                            verificationCheckHandler.postDelayed(this, 3000);
                        }
                    });
                }
            }
        };
        verificationCheckHandler.postDelayed(verificationCheckRunnable, 3000);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void backToLogin() {
        // Sign out current user
        mAuth.signOut();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (verificationCheckHandler != null && verificationCheckRunnable != null) {
            verificationCheckHandler.removeCallbacks(verificationCheckRunnable);
        }
    }
}