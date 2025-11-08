package com.example.assignmentpod.model.request;

import com.example.assignmentpod.model.building.Building;
import com.example.assignmentpod.model.servicepackage.ServicePackage;
import com.example.assignmentpod.model.user.Account;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailCreationRequest {
    
    @SerializedName("building")
    private Building building;
    
    @SerializedName("selectedRooms")
    private List<RoomWithAmenitiesDTO> selectedRooms;
    
    @SerializedName("servicePackage")
    private ServicePackage servicePackage;
    
    @SerializedName("customer")
    private Account customer;
    
    @SerializedName("priceRoom")
    private double priceRoom;
    
    @SerializedName("startTime")
    private List<String> startTime; // Using String in format "yyyy-MM-dd'T'HH:mm:ss"
    
    @SerializedName("endTime")
    private List<String> endTime; // Using String in format "yyyy-MM-dd'T'HH:mm:ss"

    // Constructors
    public OrderDetailCreationRequest() {}

    public OrderDetailCreationRequest(Building building, List<RoomWithAmenitiesDTO> selectedRooms,
                                     ServicePackage servicePackage, Account customer,
                                     double priceRoom, List<String> startTime, List<String> endTime) {
        this.building = building;
        this.selectedRooms = selectedRooms;
        this.servicePackage = servicePackage;
        this.customer = customer;
        this.priceRoom = priceRoom;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<RoomWithAmenitiesDTO> getSelectedRooms() {
        return selectedRooms;
    }

    public void setSelectedRooms(List<RoomWithAmenitiesDTO> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(ServicePackage servicePackage) {
        this.servicePackage = servicePackage;
    }

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
    }

    public double getPriceRoom() {
        return priceRoom;
    }

    public void setPriceRoom(double priceRoom) {
        this.priceRoom = priceRoom;
    }

    public List<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }

    public List<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<String> endTime) {
        this.endTime = endTime;
    }
}

