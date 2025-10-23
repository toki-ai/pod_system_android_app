package com.example.assignmentpod.utils;

import com.example.assignmentpod.model.room.RoomType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for extracting and applying filters to room types.
 * Provides methods to extract unique filter values and apply multiple filters client-side.
 */
public class FilterHelper {

    /**
     * Extract unique room type names from a list of room types.
     *
     * @param roomTypes List of room types to extract names from
     * @return List of unique room names, sorted alphabetically
     */
    public static List<String> extractUniqueRoomNames(List<RoomType> roomTypes) {
        Set<String> uniqueNames = new HashSet<>();
        for (RoomType roomType : roomTypes) {
            if (roomType != null && roomType.getName() != null && !roomType.getName().isEmpty()) {
                uniqueNames.add(roomType.getName());
            }
        }
        List<String> result = new ArrayList<>(uniqueNames);
        java.util.Collections.sort(result);
        return result;
    }

    /**
     * Extract unique addresses from building information in room types.
     *
     * @param roomTypes List of room types to extract addresses from
     * @return List of unique addresses, sorted alphabetically
     */
    public static List<String> extractUniqueAddresses(List<RoomType> roomTypes) {
        Set<String> uniqueAddresses = new HashSet<>();
        for (RoomType roomType : roomTypes) {
            if (roomType != null && roomType.getBuilding() != null 
                    && roomType.getBuilding().getAddress() != null 
                    && !roomType.getBuilding().getAddress().isEmpty()) {
                uniqueAddresses.add(roomType.getBuilding().getAddress());
            }
        }
        List<String> result = new ArrayList<>(uniqueAddresses);
        java.util.Collections.sort(result);
        return result;
    }

    /**
     * Extract unique capacities from room types.
     *
     * @param roomTypes List of room types to extract capacities from
     * @return List of unique capacities, sorted numerically
     */
    public static List<Integer> extractUniqueCapacities(List<RoomType> roomTypes) {
        Set<Integer> uniqueCapacities = new HashSet<>();
        for (RoomType roomType : roomTypes) {
            if (roomType != null && roomType.getCapacity() > 0) {
                uniqueCapacities.add(roomType.getCapacity());
            }
        }
        List<Integer> result = new ArrayList<>(uniqueCapacities);
        java.util.Collections.sort(result);
        return result;
    }

    /**
     * Calculate the price range (minimum and maximum) from a list of room types.
     *
     * @param roomTypes List of room types to analyze
     * @return Array of [minPrice, maxPrice], returns [0, 0] if list is empty
     */
    public static double[] getPriceRange(List<RoomType> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            return new double[]{0, 0};
        }

        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0;

        for (RoomType roomType : roomTypes) {
            if (roomType != null && roomType.getPrice() > 0) {
                minPrice = Math.min(minPrice, roomType.getPrice());
                maxPrice = Math.max(maxPrice, roomType.getPrice());
            }
        }

        if (minPrice == Double.MAX_VALUE) {
            minPrice = 0;
        }

        return new double[]{minPrice, maxPrice};
    }

    /**
     * Apply multiple filters to a list of room types client-side.
     * Only returns room types that match ALL specified filter criteria.
     *
     * @param roomTypes       List of room types to filter
     * @param roomTypeName    Filter by room type name (null to ignore)
     * @param minPrice        Filter by minimum price (null to ignore)
     * @param maxPrice        Filter by maximum price (null to ignore)
     * @param address         Filter by building address (null to ignore)
     * @param capacity        Filter by capacity (null to ignore)
     * @return List of room types that match all specified filters
     */
    public static List<RoomType> filterRoomTypes(List<RoomType> roomTypes,
                                                   String roomTypeName,
                                                   Double minPrice,
                                                   Double maxPrice,
                                                   String address,
                                                   Integer capacity) {
        List<RoomType> filteredList = new ArrayList<>();

        for (RoomType roomType : roomTypes) {
            if (roomType == null) {
                continue;
            }

            // Check room type name filter
            if (roomTypeName != null && !roomTypeName.isEmpty()) {
                if (roomType.getName() == null || !roomType.getName().equalsIgnoreCase(roomTypeName)) {
                    continue;
                }
            }

            // Check price range filter
            if (minPrice != null && roomType.getPrice() < minPrice) {
                continue;
            }
            if (maxPrice != null && roomType.getPrice() > maxPrice) {
                continue;
            }

            // Check address filter
            if (address != null && !address.isEmpty()) {
                if (roomType.getBuilding() == null 
                        || roomType.getBuilding().getAddress() == null 
                        || !roomType.getBuilding().getAddress().equalsIgnoreCase(address)) {
                    continue;
                }
            }

            // Check capacity filter
            if (capacity != null && capacity > 0) {
                if (roomType.getCapacity() != capacity) {
                    continue;
                }
            }

            // If all filters passed, add to result
            filteredList.add(roomType);
        }

        return filteredList;
    }

    /**
     * Simple overload for filtering with only name, price range, and address.
     * Useful for common filtering scenarios.
     *
     * @param roomTypes    List of room types to filter
     * @param roomTypeName Room type name filter (null to ignore)
     * @param minPrice     Minimum price filter (null to ignore)
     * @param maxPrice     Maximum price filter (null to ignore)
     * @param address      Address filter (null to ignore)
     * @return Filtered list of room types
     */
    public static List<RoomType> filterRoomTypes(List<RoomType> roomTypes,
                                                   String roomTypeName,
                                                   Double minPrice,
                                                   Double maxPrice,
                                                   String address) {
        return filterRoomTypes(roomTypes, roomTypeName, minPrice, maxPrice, address, null);
    }
}
