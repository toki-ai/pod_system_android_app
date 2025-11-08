package com.example.assignmentpod.model.request;

import com.example.assignmentpod.model.amenity.Amenity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoomWithAmenitiesDTO {
    
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("amenities")
    private List<Amenity> amenities; // Will be null by default

    // Constructors
    public RoomWithAmenitiesDTO() {}

    public RoomWithAmenitiesDTO(Integer id, String name, double price, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.amenities = null; // Default to null
    }

    public RoomWithAmenitiesDTO(Integer id, String name, double price, String image, List<Amenity> amenities) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.amenities = amenities;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }
}

