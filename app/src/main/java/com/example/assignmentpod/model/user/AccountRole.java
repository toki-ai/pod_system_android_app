package com.example.assignmentpod.model.user;

import com.google.gson.annotations.SerializedName;

public enum AccountRole {
    @SerializedName("Admin")
    ADMIN("Admin"),
    
    @SerializedName("Manager")
    MANAGER("Manager"),
    
    @SerializedName("Staff") 
    STAFF("Staff"),
    
    @SerializedName("Customer")
    CUSTOMER("Customer");

    private final String displayName;

    AccountRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}