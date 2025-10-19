package com.example.assignmentpod.model.room;

import com.example.assignmentpod.model.building.Building;
import com.google.gson.annotations.SerializedName;

public class RoomType {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private int price;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("image")
    private String image;

    @SerializedName("building")
    private Building building;

    public RoomType() {}

    public RoomType(Integer id, String name, int price, int quantity, int capacity, String image, Building building) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.capacity = capacity;
        this.image = image;
        this.building = building;
    }

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }
}