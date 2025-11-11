package com.example.assignmentpod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.model.auth.AuthResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private AuthRepository authRepository;
    
    // UI Components
    private TextInputEditText etEmail, etPassword;
    private TextView btnLogin, btnRegister;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        authRepository = new AuthRepository(this);

        if (authRepository.isLoggedIn()) {
            navigateToMain();
            return;
        }
        
        initViews();
        setupClickListeners();
        
        // Check for success message from registration
        String successMessage = getIntent().getStringExtra("success_message");
        if (successMessage != null) {
            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
        }
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnRegister.setOnClickListener(v -> navigateToRegister());
    }
    
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }
        
        loginUser(email, password);
    }
    
    private void loginUser(String email, String password) {
        showLoading(true);
        
        Log.d(TAG, "Attempting login with email: " + email);
        
        authRepository.login(email, password, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(AuthResponse authResponse) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    String message = "Đăng nhập thành công!";
                    if (authResponse.getAccount() != null) {
                        message += " Welcome " + authResponse.getAccount().getName();
                    }
                    
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    
                    Log.d(TAG, "Login successful for: " + email);
                    Log.d(TAG, "Access token length: " + authResponse.getAccessToken().length());

                    navigateToMain();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + error, Toast.LENGTH_LONG).show();
                    
                    Log.e(TAG, "Login failed for: " + email + ", Error: " + error);

                    etPassword.setText("");
                });
            }
        });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "" : "Đăng nhập");
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if (authRepository.isLoggedIn()) {
            navigateToMain();
        }
    }
}