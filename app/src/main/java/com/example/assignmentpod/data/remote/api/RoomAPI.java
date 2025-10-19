package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.room.RoomType;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomAPI {
    @GET("public/rooms")
    Call<PaginationResponse<List<Room>>> getPublicRooms(
            @Query("searchParams") String searchParams,
            @Query("buildingId") int buildingId,
            @Query("page") int page,
            @Query("take") int take
    );

    @GET("rooms")
    Call<PaginationResponse<List<Room>>> getRooms(
            @Query("searchParams") String searchParams,
            @Query("buildingId") int buildingId,
            @Query("page") int page,
            @Query("take") int take
    );
    
    @GET("rooms/type/{roomId}")
    Call<List<Room>> getRoomsSameType(@Path("roomId") int roomId);

    @GET("rooms/filtered-room")
    Call<PaginationResponse<List<Room>>> getFilteredRoom(
            @Query("address") String address,
            @Query("capacity") Integer capacity,
            @Query("startTime") LocalDateTime startTime,
            @Query("endTime") LocalDateTime endTime,
            @Query("page") int page,
            @Query("take") int take
    );

    @GET("room-types")
    Call<PaginationResponse<List<RoomType>>> getRoomTypes(
            @Query("page") int page,
            @Query("take") int take
    );
    
    @GET("room-types/{roomTypeId}")
    Call<ApiResponse<RoomType>> getRoomTypeById(@Path("roomTypeId") int roomTypeId);
    
    @GET("room-types/filtered-room-type")
    Call<PaginationResponse<List<RoomType>>> getFilteredRoomTypes(
            @Query("address") String address,
            @Query("capacity") Integer capacity,
            @Query("startTime") LocalDateTime startTime,
            @Query("endTime") LocalDateTime endTime,
            @Query("page") int page,
            @Query("take") int take
    );
    
    @GET("room-types/room-type-within-address")
    Call<ApiResponse<List<RoomType>>> getRoomTypesByAddress(@Query("address") String address);
    
    @GET("room-types/get-by-building-id")
    Call<ApiResponse<List<RoomType>>> getRoomTypesByBuildingId(@Query("buildingId") Integer buildingId);
}
