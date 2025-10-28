package com.example.assignmentpod.utils;

import com.example.assignmentpod.model.room.RoomType;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for filtering room types by building.
 */
public class BuildingFilterHelper {
    private static final String TAG = "BuildingFilterHelper";
    
    /**
     * Filters room types by building ID or building name.
     * This is a simple implementation - in a real app, you would have
     * a proper relationship between buildings and rooms.
     * 
     * @param roomTypes List of all room types
     * @param buildingId Building ID to filter by
     * @param buildingName Building name to filter by
     * @return Filtered list of room types
     */
    public static List<RoomType> filterByBuilding(List<RoomType> roomTypes, 
                                                Integer buildingId, 
                                                String buildingName) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RoomType> filteredRooms = new ArrayList<>();
        
        // Simple filtering logic - in a real app, you would check room.buildingId
        // For now, we'll filter by room name containing building keywords
        String[] buildingKeywords = {
            "Nguyen Hue", "Le Lai", "Pham Ngu Lao", 
            "Vo Van Tan", "Tran Hung Dao", "District 1", 
            "District 3", "District 5", "HCMC"
        };
        
        for (RoomType roomType : roomTypes) {
            boolean matchesBuilding = false;
            
            if (buildingName != null) {
                // Check if room name contains building-related keywords
                String roomName = roomType.getName().toLowerCase();
                
                for (String keyword : buildingKeywords) {
                    if (roomName.contains(keyword.toLowerCase())) {
                        matchesBuilding = true;
                        break;
                    }
                }
                
                // Also check if building name contains any of the keywords
                String buildingNameLower = buildingName.toLowerCase();
                for (String keyword : buildingKeywords) {
                    if (buildingNameLower.contains(keyword.toLowerCase())) {
                        matchesBuilding = true;
                        break;
                    }
                }
            }
            
            if (matchesBuilding) {
                filteredRooms.add(roomType);
            }
        }
        
        return filteredRooms;
    }
    
    /**
     * Gets building name from room type (mock implementation).
     * In a real app, this would come from the room's building relationship.
     */
    public static String getBuildingNameFromRoom(RoomType roomType) {
        // Mock implementation - return a default building name
        return "Main Building";
    }
}
