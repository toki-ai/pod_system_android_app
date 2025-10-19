package com.example.assignmentpod.model.building;

import com.google.gson.annotations.SerializedName;

public class Building {
    @SerializedName("id")
    private Integer id;

    @SerializedName("status")
    private BuildingStatus status;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("hotlineNumber")
    private String hotlineNumber;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public Building() {}

    public Building(Integer id, BuildingStatus status, String address, String description, String hotlineNumber, String createdAt, String updatedAt) {
        this.id = id;
        this.status = status;
        this.address = address;
        this.description = description;
        this.hotlineNumber = hotlineNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BuildingStatus getStatus() {
        return status;
    }

    public void setStatus(BuildingStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHotlineNumber() {
        return hotlineNumber;
    }

    public void setHotlineNumber(String hotlineNumber) {
        this.hotlineNumber = hotlineNumber;
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
}