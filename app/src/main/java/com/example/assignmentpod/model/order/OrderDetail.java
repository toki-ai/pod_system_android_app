package com.example.assignmentpod.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDetail {
    @SerializedName("id")
    private String id;

    @SerializedName("orderDetailId")
    private String orderDetailId;

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerEmail")
    private String customerEmail;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("buildingId")
    private int buildingId;

    @SerializedName("buildingAddress")
    private String buildingAddress;

    @SerializedName("roomId")
    private int roomId;

    @SerializedName("roomName")
    private String roomName;

    @SerializedName("roomImage")
    private String roomImage;

    @SerializedName("roomType")
    private String roomType;

    @SerializedName("roomPrice")
    private double roomPrice;

    @SerializedName("priceRoom")
    private double priceRoom;

    @SerializedName("discountPercentage")
    private double discountPercentage;

    @SerializedName("servicePackageId")
    private int servicePackageId;

    @SerializedName("servicePackageName")
    private String servicePackageName;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("duration")
    private String duration;

    @SerializedName("status")
    private String status;

    @SerializedName("orderHandler")
    private OrderHandler orderHandler;

    @SerializedName("amenities")
    private List<Amenity> amenities;

    @SerializedName("orderDetailAmenities")
    private List<Amenity> orderDetailAmenities;

    @SerializedName("billingBreakdown")
    private BillingBreakdown billingBreakdown;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("cancelReason")
    private String cancelReason;

    // Constructors
    public OrderDetail() {}

    public OrderDetail(String id, String orderDetailId, String orderId, String customerId,
                      String customerName, String customerEmail, String customerPhone,
                      int buildingId, String buildingAddress, int roomId, String roomName,
                      String roomImage, String roomType, double roomPrice, double discountPercentage,
                      int servicePackageId, String servicePackageName, String startTime,
                      String endTime, String duration, String status, OrderHandler orderHandler,
                      List<Amenity> amenities, BillingBreakdown billingBreakdown,
                      String createdAt, String cancelReason) {
        this.id = id;
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.buildingId = buildingId;
        this.buildingAddress = buildingAddress;
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomImage = roomImage;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.discountPercentage = discountPercentage;
        this.servicePackageId = servicePackageId;
        this.servicePackageName = servicePackageName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.status = status;
        this.orderHandler = orderHandler;
        this.amenities = amenities;
        this.billingBreakdown = billingBreakdown;
        this.createdAt = createdAt;
        this.cancelReason = cancelReason;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(String orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingAddress() {
        return buildingAddress;
    }

    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomImage() {
        return roomImage;
    }

    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public double getPriceRoom() {
        return priceRoom;
    }

    public void setPriceRoom(double priceRoom) {
        this.priceRoom = priceRoom;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getServicePackageId() {
        return servicePackageId;
    }

    public void setServicePackageId(int servicePackageId) {
        this.servicePackageId = servicePackageId;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderHandler getOrderHandler() {
        return orderHandler;
    }

    public void setOrderHandler(OrderHandler orderHandler) {
        this.orderHandler = orderHandler;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public List<Amenity> getOrderDetailAmenities() {
        return orderDetailAmenities;
    }

    public void setOrderDetailAmenities(List<Amenity> orderDetailAmenities) {
        this.orderDetailAmenities = orderDetailAmenities;
    }

    public BillingBreakdown getBillingBreakdown() {
        return billingBreakdown;
    }

    public void setBillingBreakdown(BillingBreakdown billingBreakdown) {
        this.billingBreakdown = billingBreakdown;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
