package com.example.assignmentpod.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.AuthRepository;
import com.example.assignmentpod.model.response.AccountResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    
    private AuthRepository authRepository;
    private FirebaseAuth mAuth;
    
    // UI Components
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private TextView btnRegister, btnBackToLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        
        mAuth = FirebaseAuth.getInstance();
        authRepository = new AuthRepository(this);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        btnBackToLogin.setOnClickListener(v -> navigateToLogin());
    }
    
    private void performRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        
        if (name.length() < 5) {
            etName.setError("Name must be at least 5 characters");
            etName.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        
        registerUser(name, email, password);
    }
    
    private void registerUser(String name, String email, String password) {
        showLoading(true);
        
        Log.d(TAG, "Creating Firebase user with email: " + email);
        
        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Firebase user created successfully");
                    FirebaseUser user = mAuth.getCurrentUser();
                    
                    if (user != null) {
                        // Send email verification
                        sendEmailVerification(user, name, email, password);
                    }
                } else {
                    showLoading(false);
                    Log.e(TAG, "Firebase user creation failed", task.getException());
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void sendEmailVerification(FirebaseUser user, String name, String email, String password) {
        user.sendEmailVerification()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email verification sent to: " + email);
                    
                    // Also register with your backend
                    registerWithBackend(name, email, password);
                } else {
                    showLoading(false);
                    Log.e(TAG, "Failed to send verification email", task.getException());
                    
                    // Delete the Firebase user since verification failed
                    user.delete();
                    
                    Toast.makeText(RegisterActivity.this, "Failed to send verification email. Please try again.", Toast.LENGTH_LONG).show();
                }
            });
    }
    
    private void registerWithBackend(String name, String email, String password) {
        authRepository.register(name, email, password, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(AccountResponse accountResponse) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    Log.d(TAG, "Backend registration successful for: " + email);
                    
                    // Navigate to email verification screen
                    navigateToEmailVerification();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    Log.e(TAG, "Backend registration failed for: " + email + ", Error: " + error);
                    
                    // Delete Firebase user since backend registration failed
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.delete();
                    }
                    
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void navigateToEmailVerification() {
        Intent intent = new Intent(this, EmailVerificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
        btnRegister.setText(show ? "" : "Create Account");
        
        // Disable form during loading
        etName.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
        etConfirmPassword.setEnabled(!show);
        btnBackToLogin.setEnabled(!show);
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void navigateToLoginWithMessage(String message) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("success_message", message);
        startActivity(intent);
        finish();
    }
}