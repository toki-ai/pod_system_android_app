package com.example.assignmentpod.data.remote.api;

import com.example.assignmentpod.model.order.Order;
import com.example.assignmentpod.model.order.OrderDetail;
import com.example.assignmentpod.model.request.OrderDetailCreationRequest;
import com.example.assignmentpod.model.response.ApiResponse;
import com.example.assignmentpod.model.response.PaginationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderAPI {
    
    /**
     * Get customer order history with pagination
     * @param accountId The customer's account ID
     * @param page Page number (default: 0, 0-indexed)
     * @param take Records per page (default: 5)
     * @param status Filter by order status (optional, default: "Successfully")
     * @return Paginated list of orders with their details
     */
    @GET("order/{accountId}")
    Call<PaginationResponse<List<Order>>> getCustomerOrderHistory(
            @Path("accountId") String accountId,
            @Query("page") int page,
            @Query("take") int take,
            @Query("status") String status
    );

    /**
     * Get order detail with full information
     * @param orderDetailId The order detail ID
     * @return Complete order detail information including amenities and billing
     */
    @GET("order-detail/{orderDetailId}")
    Call<ApiResponse<OrderDetail>> getOrderDetail(
            @Path("orderDetailId") String orderDetailId
    );

    /**
     * Get order details by customer ID with pagination
     * @param customerId The customer's account ID
     * @param page Page number (default: 1, 1-indexed)
     * @param take Records per page (default: 3)
     * @param status Filter by status (optional, default: "Successfully")
     * @return Paginated list of order details
     */
    @GET("order-detail/customer/{customerId}")
    Call<PaginationResponse<List<OrderDetail>>> getOrderDetailsByCustomer(
            @Path("customerId") String customerId,
            @Query("page") int page,
            @Query("take") int take,
            @Query("status") String status
    );

    /**
     * Create a new order
     * @param authorization Authorization header with Bearer token
     * @param request Order creation request with all booking details
     * @return API response with order status (Successfully or Pending)
     */
    @POST("order")
    Call<ApiResponse<String>> createOrder(
            @Header("Authorization") String authorization,
            @Body OrderDetailCreationRequest request
    );
}
