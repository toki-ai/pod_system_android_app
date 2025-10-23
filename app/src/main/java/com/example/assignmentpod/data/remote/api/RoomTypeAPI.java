package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.model.room.RoomType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API interface for room type operations
 * Provides endpoints to fetch room types with optional filtering
 */
public interface RoomTypeAPI {

    /**
     * Fetch filtered room types with pagination support
     * 
     * @param page Page number (optional, default: 1)
     * @param take Records per page (optional, default: 10)
     * @param address Filter by building address (optional)
     * @param capacity Filter by room capacity (optional)
     * @return Call with PaginationResponse containing list of RoomType
     */
    @GET("room-types/filtered-room-type")
    Call<ApiResponse<PaginationResponse<List<RoomType>>>> getFilteredRoomTypes(
            @Query("page") int page,
            @Query("take") int take
    );

    /**
     * Fetch filtered room types with address filter
     */
    @GET("room-types/filtered-room-type")
    Call<ApiResponse<PaginationResponse<List<RoomType>>>> getFilteredRoomTypesByAddress(
            @Query("page") int page,
            @Query("take") int take,
            @Query("address") String address
    );

    /**
     * Fetch filtered room types with capacity filter
     */
    @GET("room-types/filtered-room-type")
    Call<ApiResponse<PaginationResponse<List<RoomType>>>> getFilteredRoomTypesByCapacity(
            @Query("page") int page,
            @Query("take") int take,
            @Query("capacity") int capacity
    );

    /**
     * Fetch filtered room types with both address and capacity filters
     */
    @GET("room-types/filtered-room-type")
    Call<ApiResponse<PaginationResponse<List<RoomType>>>> getFilteredRoomTypesByAddressAndCapacity(
            @Query("page") int page,
            @Query("take") int take,
            @Query("address") String address,
            @Query("capacity") int capacity
    );
}
