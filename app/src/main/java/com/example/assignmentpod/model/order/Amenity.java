package com.example.assignmentpod.model.order;

import com.google.gson.annotations.SerializedName;

public class Amenity {
    @SerializedName("id")
    private String id;

    @SerializedName("amenityName")
    private String amenityName;

    @SerializedName("amenityType")
    private String amenityType;

    @SerializedName("amenityImage")
    private String amenityImage;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unitPrice")
    private double unitPrice;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("status")
    private String status;

    // Constructors
    public Amenity() {}

    public Amenity(String id, String amenityName, String amenityType, String amenityImage,
                   int quantity, double unitPrice, double totalPrice, String status) {
        this.id = id;
        this.amenityName = amenityName;
        this.amenityType = amenityType;
        this.amenityImage = amenityImage;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }

    public String getAmenityType() {
        return amenityType;
    }

    public void setAmenityType(String amenityType) {
        this.amenityType = amenityType;
    }

    public String getAmenityImage() {
        return amenityImage;
    }

    public void setAmenityImage(String amenityImage) {
        this.amenityImage = amenityImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
