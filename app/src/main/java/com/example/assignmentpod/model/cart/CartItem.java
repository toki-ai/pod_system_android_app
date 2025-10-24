package com.example.assignmentpod.model.cart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int roomId;
    private String roomName;
    private String roomDescription;
    private String roomImage;
    private String roomTypeName;
    private double roomPrice;
    private long addedAt;
    
    public CartItem() {
        this.addedAt = System.currentTimeMillis();
    }
    
    public CartItem(int roomId, String roomName, String roomDescription, String roomImage, String roomTypeName, double roomPrice) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.roomImage = roomImage;
        this.roomTypeName = roomTypeName;
        this.roomPrice = roomPrice;
        this.addedAt = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public String getRoomDescription() {
        return roomDescription;
    }
    
    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }
    
    public String getRoomImage() {
        return roomImage;
    }
    
    public void setRoomImage(String roomImage) {
        this.roomImage = roomImage;
    }
    
    public String getRoomTypeName() {
        return roomTypeName;
    }
    
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
    
    public double getRoomPrice() {
        return roomPrice;
    }
    
    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }
    
    public long getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }
}
