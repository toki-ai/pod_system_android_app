package com.example.assignmentpod.data.remote.api;

import android.content.Context;

import com.example.assignmentpod.data.local.TokenManager;
import com.example.assignmentpod.data.remote.interceptor.TokenAuthenticator;
import com.example.assignmentpod.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = Constants.BASE_URL;
    
    private static Retrofit publicInstance;
    private static Retrofit authenticatedInstance;
    private static Context applicationContext;
    
    public static void initialize(Context context) {
        applicationContext = context.getApplicationContext();
    }

    public static Retrofit getPublicInstance() {
        if (publicInstance == null) {
            publicInstance = buildPublicRetrofit();
        }
        return publicInstance;
    }

    public static Retrofit getAuthenticatedInstance() {
        if (authenticatedInstance == null) {
            if (applicationContext == null) {
                throw new IllegalStateException("RetrofitClient must be initialized with context first");
            }
            authenticatedInstance = buildAuthenticatedRetrofit(applicationContext);
        }
        return authenticatedInstance;
    }

    private static Retrofit buildPublicRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static Retrofit buildAuthenticatedRetrofit(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(context, BASE_URL);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(tokenAuthenticator)
                .addInterceptor(logging)
                .build();
        
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static TokenManager getTokenManager() {
        if (applicationContext == null) {
            throw new IllegalStateException("RetrofitClient must be initialized with context first");
        }
        return new TokenManager(applicationContext);
    }

    public static void clearInstances() {
        publicInstance = null;
        authenticatedInstance = null;
        
        if (applicationContext != null) {
            TokenManager tokenManager = new TokenManager(applicationContext);
            tokenManager.clearTokens();
        }
    }

    public static boolean isAuthenticated() {
        if (applicationContext == null) {
            return false;
        }
        TokenManager tokenManager = new TokenManager(applicationContext);
        return tokenManager.hasValidTokens();
    }
}