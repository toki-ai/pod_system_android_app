package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.servicepackage.ServicePackage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServicePackageAPI {
    @GET("service-package")
    Call<ApiResponse<List<ServicePackage>>> getServicePackages();
}
