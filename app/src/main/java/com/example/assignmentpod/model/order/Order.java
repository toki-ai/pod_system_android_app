package com.example.assignmentpod.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Order {
    @SerializedName("orderId")
    private String orderId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("discountAmount")
    private double discountAmount;

    @SerializedName("finalAmount")
    private double finalAmount;

    @SerializedName("orderStatus")
    private String orderStatus;

    @SerializedName("orderDetails")
    private List<OrderDetail> orderDetails;

    // Constructors
    public Order() {}

    public Order(String orderId, String createdAt, String updatedAt, double totalAmount,
                double discountAmount, double finalAmount, String orderStatus,
                List<OrderDetail> orderDetails) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.orderStatus = orderStatus;
        this.orderDetails = orderDetails;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
