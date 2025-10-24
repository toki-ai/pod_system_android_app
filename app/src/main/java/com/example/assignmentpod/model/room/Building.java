package com.example.assignmentpod.model.room;

import com.google.gson.annotations.SerializedName;

/**
 * Building information for a room type
 */
public class Building {
    @SerializedName("id")
    private int id;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    @SerializedName("hotlineNumber")
    private String hotlineNumber;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public Building() {}

    public Building(int id, String address, String description, String hotlineNumber, String status, String createdAt) {
        this.id = id;
        this.address = address;
        this.description = description;
        this.hotlineNumber = hotlineNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
