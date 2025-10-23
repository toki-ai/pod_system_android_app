package com.example.assignmentpod.data.repository;

import androidx.annotation.NonNull;

import com.example.assignmentpod.data.remote.api.RoomAPI;
import com.example.assignmentpod.data.remote.api.RetrofitClient;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.room.RoomResponse;
import com.example.assignmentpod.model.slot.SlotDTO;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {
    private final RoomAPI publicAPI;
    private final RoomAPI authenticatedAPI;

    public RoomRepository() {
        this.publicAPI = RetrofitClient.getPublicInstance().create(RoomAPI.class);
        this.authenticatedAPI = RetrofitClient.getAuthenticatedInstance().create(RoomAPI.class);
    }

    public interface SlotListCallback {
        void onSuccess(List<SlotDTO> slots);

        void onError(String error);
    }

    public interface RoomsCallback {
        void onSuccess(PaginationResponse<List<Room>> response);

        void onError(String error);
    }

    public interface RoomListCallback {
        void onSuccess(List<Room> rooms);

        void onError(String error);
    }

    public void getPublicRooms(String searchParams, int buildingId, int page, int take, RoomsCallback callback) {
        Call<PaginationResponse<List<Room>>> call = publicAPI.getPublicRooms(searchParams, buildingId, page, take);
        call.enqueue(new Callback<PaginationResponse<List<Room>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<Room>>> call, Response<PaginationResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<List<Room>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRooms(String searchParams, int buildingId, int page, int take, RoomsCallback callback) {
        Call<PaginationResponse<List<Room>>> call = authenticatedAPI.getRooms(searchParams, buildingId, page, take);
        call.enqueue(new Callback<PaginationResponse<List<Room>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<Room>>> call, Response<PaginationResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<List<Room>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getRoomsSameType(int roomId, RoomListCallback callback) {
        Call<List<Room>> call = authenticatedAPI.getRoomsSameType(roomId);
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getFilteredRoom(String address, Integer capacity, LocalDateTime startTime,
                                LocalDateTime endTime, int page, int take, RoomsCallback callback) {
        Call<PaginationResponse<List<Room>>> call = authenticatedAPI.getFilteredRoom(
                address, capacity, startTime, endTime, page, take);
        call.enqueue(new Callback<PaginationResponse<List<Room>>>() {
            @Override
            public void onResponse(Call<PaginationResponse<List<Room>>> call, Response<PaginationResponse<List<Room>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get filtered rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PaginationResponse<List<Room>>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAvailableRoomsByRoomTypeId(int typeId, List<String> slots, RoomListCallback callback) {
        Call<List<Room>> call = authenticatedAPI.getAvailableRoomsByRoomTypeId(typeId, slots);
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get available rooms: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getAvailableRoomsByTypeAndDate(int typeId, String date, RoomListCallback callback) {
        Call<RoomResponse> call = authenticatedAPI.getAvailableRoomsByTypeAndDate(typeId, date);
        call.enqueue(new Callback<RoomResponse>() {
            @Override
            public void onResponse(@NonNull Call<RoomResponse> call, @NonNull Response<RoomResponse> response) {
                System.out.println("response la: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to get available rooms by date: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RoomResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getSlotsByRoomsAndDate(List<Integer> roomIds, String date, SlotListCallback callback) {
        Call<List<SlotDTO>> call = authenticatedAPI.getSlotsByRoomsAndDate(roomIds, date);
        call.enqueue(new Callback<List<SlotDTO>>() {
            @Override
            public void onResponse(Call<List<SlotDTO>> call, Response<List<SlotDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get slots: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<SlotDTO>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
