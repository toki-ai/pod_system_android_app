package com.example.assignmentpod.model.servicepackage;

import com.google.gson.annotations.SerializedName;

public class ServicePackage {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("discountPercentage")
    private int discountPercentage;

    //TODO: ServicePackage has the one-to-many relationships with OrderDetail

    public ServicePackage(Integer id, String name, int discountPercentage) {
        this.id = id;
        this.name = name;
        this.discountPercentage = discountPercentage;
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

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}
