package com.example.assignmentpod.model.order;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BillingBreakdown {
    @SerializedName("roomSubtotal")
    private double roomSubtotal;

    @SerializedName("amenitiesSubtotal")
    private double amenitiesSubtotal;

    @SerializedName("subtotal")
    private double subtotal;

    @SerializedName("discount")
    private double discount;

    @SerializedName("serviceFee")
    private double serviceFee;

    @SerializedName("tax")
    private double tax;

    @SerializedName("totalAmount")
    private double totalAmount;

    // Constructors
    public BillingBreakdown() {}

    public BillingBreakdown(double roomSubtotal, double amenitiesSubtotal, double subtotal,
                           double discount, double serviceFee, double tax, double totalAmount) {
        this.roomSubtotal = roomSubtotal;
        this.amenitiesSubtotal = amenitiesSubtotal;
        this.subtotal = subtotal;
        this.discount = discount;
        this.serviceFee = serviceFee;
        this.tax = tax;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public double getRoomSubtotal() {
        return roomSubtotal;
    }

    public void setRoomSubtotal(double roomSubtotal) {
        this.roomSubtotal = roomSubtotal;
    }

    public double getAmenitiesSubtotal() {
        return amenitiesSubtotal;
    }

    public void setAmenitiesSubtotal(double amenitiesSubtotal) {
        this.amenitiesSubtotal = amenitiesSubtotal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
