package com.example.assignmentpod.model.building;

/**
 * Model class representing a building from the JSON data.
 */
public class Building {
    private int id;
    private String status;
    private String address;
    private String description;
    private String hotlineNumber;
    private String createdAt;
    private String updatedAt;
    private double latitude;
    private double longitude;

    // Default constructor
    public Building() {}

    // Constructor with all parameters
    public Building(int id, String status, String address, String description, 
                   String hotlineNumber, String createdAt, String updatedAt, 
                   double latitude, double longitude) {
        this.id = id;
        this.status = status;
        this.address = address;
        this.description = description;
        this.hotlineNumber = hotlineNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", hotlineNumber='" + hotlineNumber + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}