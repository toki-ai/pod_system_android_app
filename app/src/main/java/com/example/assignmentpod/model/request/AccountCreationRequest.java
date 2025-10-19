package com.example.assignmentpod.model.request;

import com.google.gson.annotations.SerializedName;

public class AccountCreationRequest {
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("buildingNumber")
    private int buildingNumber;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("status")
    private int status;
    
    public AccountCreationRequest() {}
    
    public AccountCreationRequest(String name, String email, String password, int buildingNumber, String role, int status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.buildingNumber = buildingNumber;
        this.role = role;
        this.status = status;
    }
    
    // Getters and Setters
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
    
    public int getBuildingNumber() {
        return buildingNumber;
    }
    
    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}