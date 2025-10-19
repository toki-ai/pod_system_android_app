package com.example.assignmentpod.data.repository;

import com.example.assignmentpod.data.remote.api.RoomAPI;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.model.room.RoomType;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomTypeRepository {
    private final RoomAPI publicAPI;
    private final RoomAPI authenticatedAPI;
    
    public RoomTypeRepository() {
        this.publicAPI = RetrofitClient.getPublicInstance().create(RoomAPI.class);
        this.authenticatedAPI = RetrofitClient.getAuthenticatedInstance().create(RoomAPI.class);
    }
    
    public interface RoomTypesCallback {
        void onSuccess(PaginationResponse<List<RoomType>> response);
        void onError(String error);
    }
    
    public interface RoomTypeCallback {
        void onSuccess(RoomType roomType);
        void onError(String error);
    }
    
    public interface RoomTypeListCallback {
        void onSuccess(List<RoomType> roomTypes);
        void onError(String error);
    }

    public void getRoomTypes(int page, int take, RoomTypesCallback callback) {
        Call<PaginationResponse<List<RoomType>>> call = authenticatedAPI.getRoomTypes(page, take);
        call.enqueue(new Callback<PaginationResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<RoomType>>> call, Response<PaginationResponse<List<RoomType>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get room types: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<PaginationResponse<List<RoomType>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRoomTypeById(int roomTypeId, RoomTypeCallback callback) {
        Call<ApiResponse<RoomType>> call = authenticatedAPI.getRoomTypeById(roomTypeId);
        call.enqueue(new Callback<ApiResponse<RoomType>>() {
            @Override
            public void onResponse(Call<ApiResponse<RoomType>> call, Response<ApiResponse<RoomType>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : response.message();
                    callback.onError("Failed to get room type: " + errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<RoomType>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getFilteredRoomTypes(String address, Integer capacity, LocalDateTime startTime, 
                                   LocalDateTime endTime, int page, int take, RoomTypesCallback callback) {
        Call<PaginationResponse<List<RoomType>>> call = authenticatedAPI.getFilteredRoomTypes(
                address, capacity, startTime, endTime, page, take);
        call.enqueue(new Callback<PaginationResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<RoomType>>> call, Response<PaginationResponse<List<RoomType>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get filtered room types: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<PaginationResponse<List<RoomType>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRoomTypesByAddress(String address, RoomTypeListCallback callback) {
        Call<ApiResponse<List<RoomType>>> call = authenticatedAPI.getRoomTypesByAddress(address);
        call.enqueue(new Callback<ApiResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RoomType>>> call, Response<ApiResponse<List<RoomType>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : response.message();
                    callback.onError("Failed to get room types by address: " + errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<RoomType>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRoomTypesByBuildingId(Integer buildingId, RoomTypeListCallback callback) {
        Call<ApiResponse<List<RoomType>>> call = authenticatedAPI.getRoomTypesByBuildingId(buildingId);
        call.enqueue(new Callback<ApiResponse<List<RoomType>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<RoomType>>> call, Response<ApiResponse<List<RoomType>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : response.message();
                    callback.onError("Failed to get room types by building ID: " + errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<RoomType>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}