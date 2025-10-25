package com.example.assignmentpod.data.repository;

import androidx.annotation.NonNull;

import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.data.remote.api.ServicePackageAPI;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.servicepackage.ServicePackage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicePackageRepository {
    private final ServicePackageAPI authenticatedServicePackageAPI;

    public ServicePackageRepository() {
        this.authenticatedServicePackageAPI = RetrofitClient.getAuthenticatedInstance().create(ServicePackageAPI.class);
    }

    public interface ServicePackageCallback {
        void onSuccess(List<ServicePackage> servicePackages);

        void onError(String error);
    }

    public void getServicePackages(ServicePackageCallback callback) {
        Call<ApiResponse<List<ServicePackage>>> call = authenticatedServicePackageAPI.getServicePackages();
        call.enqueue(new Callback<ApiResponse<List<ServicePackage>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ServicePackage>>> call, @NonNull Response<ApiResponse<List<ServicePackage>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to get service packages: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ServicePackage>>> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

}
