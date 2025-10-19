package com.example.assignmentpod.model.user;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("balance")
    private Double balance; // Change to Double to handle null
    
    @SerializedName("rankingName")
    private String rankingName;
    
    @SerializedName("point")
    private Integer point; // Change to Integer to handle null
    
    @SerializedName("role")
    private AccountRole role;
    
    @SerializedName("buildingNumber")
    private Integer buildingNumber; // Change to Integer to handle null

    // Constructors
    public UserProfile() {}

    public UserProfile(String id, String name, String email, String avatar, 
                      String phoneNumber, Double balance, String rankingName, 
                      Integer point, AccountRole role, Integer buildingNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.rankingName = rankingName;
        this.point = point;
        this.role = role;
        this.buildingNumber = buildingNumber;
    }

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getRankingName() {
        return rankingName;
    }

    public void setRankingName(String rankingName) {
        this.rankingName = rankingName;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public Integer getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(Integer buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
    
    /**
     * Get user initials for avatar display
     */
    public String getInitials() {
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        
        String[] words = name.trim().split("\\s+");
        if (words.length == 1) {
            return words[0].substring(0, 1).toUpperCase();
        } else {
            return (words[0].substring(0, 1) + words[words.length - 1].substring(0, 1)).toUpperCase();
        }
    }
}