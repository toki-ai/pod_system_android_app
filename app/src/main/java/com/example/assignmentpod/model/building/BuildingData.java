package com.example.assignmentpod.model.building;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Wrapper class for building data from JSON.
 */
public class BuildingData {
    @SerializedName("buildings")
    private List<Building> buildings;

    public BuildingData() {}

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }
}
