package com.example.assignmentpod.model.response;

import com.google.gson.annotations.SerializedName;

public class AccountResponse {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("point")
    private int point;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("balance")
    private double balance;
    
    @SerializedName("buildingNumber")
    private int buildingNumber;
    
    @SerializedName("rankingName")
    private String rankingName;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("status")
    private int status;
    
    public AccountResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public int getPoint() {
        return point;
    }
    
    public void setPoint(int point) {
        this.point = point;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public int getBuildingNumber() {
        return buildingNumber;
    }
    
    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
    
    public String getRankingName() {
        return rankingName;
    }
    
    public void setRankingName(String rankingName) {
        this.rankingName = rankingName;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}