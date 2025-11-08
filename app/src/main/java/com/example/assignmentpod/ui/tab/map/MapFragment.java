package com.example.assignmentpod.ui.tab.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.assignmentpod.R;
import com.example.assignmentpod.location.LocationHelper;
import com.example.assignmentpod.model.building.Building;
import com.example.assignmentpod.model.building.BuildingData;
import com.example.assignmentpod.utils.JsonUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.assignmentpod.location.DirectionsHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    // Fixed user location: Số 1 Lưu Hữu Phước, Đông Hoà, Dĩ An, Thành phố Hồ Chí Minh
    private static final double USER_LATITUDE = 10.875926;
    private static final double USER_LONGITUDE = 106.800705;

    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private DirectionsHelper directionsHelper;
    private List<Building> buildings = new ArrayList<>();
    private Building selectedBuilding;
    private Polyline currentRoute;
    
    // UI Components
    private View cardBuildingInfo;
    private TextView tvBuildingAddress;
    private TextView tvBuildingDescription;
    private TextView tvBuildingHotline;
    private Button btnViewRooms;

    public MapFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MapFragment created");
        locationHelper = new LocationHelper(requireContext());
        
        // Initialize DirectionsHelper with API key
        try {
            String apiKey = com.example.assignmentpod.BuildConfig.GOOGLE_MAPS_API_KEY;
            Log.d(TAG, "API Key from BuildConfig: " + (apiKey != null ? "Available (length: " + apiKey.length() + ")" : "NULL"));
            
            if (apiKey != null && !apiKey.equals("YOUR_API_KEY_HERE") && !apiKey.isEmpty()) {
                directionsHelper = new DirectionsHelper(apiKey);
                Log.d(TAG, "DirectionsHelper initialized successfully");
            } else {
                Log.e(TAG, "Google Maps API key not available for Directions - apiKey: " + apiKey);
                Toast.makeText(requireContext(), "API key không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing DirectionsHelper", e);
            Toast.makeText(requireContext(), "Lỗi khởi tạo DirectionsHelper: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "MapFragment onCreateView");
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "MapFragment onViewCreated");
        
        // Debug: Check if API key is available
        try {
            String apiKey = com.example.assignmentpod.BuildConfig.GOOGLE_MAPS_API_KEY;
            Log.d(TAG, "Google Maps API Key: " + (apiKey != null ? "Available" : "NULL"));
            Log.d(TAG, "API Key length: " + (apiKey != null ? apiKey.length() : 0));
        } catch (Exception e) {
            Log.e(TAG, "Error getting API key from BuildConfig", e);
        }
        
        initViews(view);
        loadBuildingData();
        setupMapFragment();
        setupClickListeners();
    }

    private void initViews(View view) {
        cardBuildingInfo = view.findViewById(R.id.card_building_info);
        tvBuildingAddress = view.findViewById(R.id.tv_building_address);
        tvBuildingDescription = view.findViewById(R.id.tv_building_description);
        tvBuildingHotline = view.findViewById(R.id.tv_building_hotline);
        btnViewRooms = view.findViewById(R.id.btn_view_rooms);
    }

    private void loadBuildingData() {
        try {
            String jsonString = JsonUtils.readJsonFromAssets(requireContext(), "building.json");
            if (jsonString != null) {
                Gson gson = new Gson();
                BuildingData buildingData = gson.fromJson(jsonString, BuildingData.class);
                if (buildingData != null && buildingData.getBuildings() != null) {
                    buildings = buildingData.getBuildings();
                    Log.d(TAG, "Loaded " + buildings.size() + " buildings from JSON");
                } else {
                    Log.e(TAG, "Failed to parse building data");
                }
            } else {
                Log.e(TAG, "Failed to read building.json from assets");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading building data", e);
            Toast.makeText(requireContext(), "Lỗi tải dữ liệu tòa nhà", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            Log.d(TAG, "MapFragment found, calling getMapAsync");
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "MapFragment not found!");
        }
        
        // Debug: Check if Google Play Services is available
        try {
            int resultCode = com.google.android.gms.common.GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(requireContext());
            Log.d(TAG, "Google Play Services availability: " + resultCode);
            if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                Log.e(TAG, "Google Play Services not available: " + resultCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking Google Play Services", e);
        }
    }

    private void setupClickListeners() {
        btnViewRooms.setOnClickListener(v -> {
            if (selectedBuilding != null) {
                // Just show building info, no navigation needed
                Toast.makeText(requireContext(), 
                    "Thông tin tòa nhà: " + selectedBuilding.getAddress(), 
                    Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Building info displayed: " + selectedBuilding.getAddress());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        Log.d(TAG, "Map is ready - GoogleMap object: " + (googleMap != null ? "NOT NULL" : "NULL"));

        // Enable user location if permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            Log.d(TAG, "Location enabled");
        } else {
            Log.d(TAG, "Location permission not granted, requesting...");
            requestLocationPermission();
        }

        // Set up marker click listener
        googleMap.setOnMarkerClickListener(this);

        // Add user location marker (fixed address)
        addUserLocationMarker();

        // Add building markers
        addBuildingMarkers();

        // Center map on user location
        LatLng userLocation = new LatLng(USER_LATITUDE, USER_LONGITUDE);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        Log.d(TAG, "Map camera moved to user location");
        
        // Debug: Check map type
        Log.d(TAG, "Map type: " + googleMap.getMapType());
        Log.d(TAG, "Map is ready - UI settings: " + googleMap.getUiSettings());
    }
    
    private void addUserLocationMarker() {
        if (googleMap == null) {
            return;
        }
        
        LatLng userLocation = new LatLng(USER_LATITUDE, USER_LONGITUDE);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(userLocation)
                .title("Vị trí của bạn")
                .snippet("Số 1 Lưu Hữu Phước, Đông Hoà, Dĩ An, Hồ Chí Minh")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        
        googleMap.addMarker(markerOptions);
        Log.d(TAG, "Added user location marker");
    }

    private void addBuildingMarkers() {
        if (googleMap == null || buildings.isEmpty()) {
            return;
        }

        for (Building building : buildings) {
            LatLng location = new LatLng(building.getLatitude(), building.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(building.getAddress())
                    .snippet(building.getDescription());

            Marker marker = googleMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(building);
            }
        }

        Log.d(TAG, "Added " + buildings.size() + " markers to map");
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Log.d(TAG, "Marker clicked");
        Building building = (Building) marker.getTag();
        if (building != null) {
            Log.d(TAG, "Building found: " + building.getAddress());
            selectedBuilding = building;
            showBuildingInfo(building);
            
            // Move camera to marker
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            
            // Draw route from user location to building
            LatLng userLocation = new LatLng(USER_LATITUDE, USER_LONGITUDE);
            LatLng destination = new LatLng(building.getLatitude(), building.getLongitude());
            Log.d(TAG, "Calling drawRoute from user location to building");
            drawRoute(userLocation, destination);
            
            return true; // Consume the event
        } else {
            Log.w(TAG, "Building is null for clicked marker");
        }
        return false;
    }

    private void showBuildingInfo(Building building) {
        tvBuildingAddress.setText(building.getAddress());
        tvBuildingDescription.setText(building.getDescription());
        tvBuildingHotline.setText("Hotline: " + building.getHotlineNumber());
        
        cardBuildingInfo.setVisibility(View.VISIBLE);
        Log.d(TAG, "Showing info for building: " + building.getAddress());
    }
    
    private void drawRoute(LatLng origin, LatLng destination) {
        Log.d(TAG, "drawRoute called - origin: (" + origin.latitude + ", " + origin.longitude + 
              "), destination: (" + destination.latitude + ", " + destination.longitude + ")");
        
        if (directionsHelper == null) {
            Log.w(TAG, "DirectionsHelper is NULL - using backup simple route");
            // Use backup method to draw simple route
            drawSimpleRoute(origin, destination);
            return;
        }
        
        // Remove previous route if exists
        if (currentRoute != null) {
            currentRoute.remove();
            Log.d(TAG, "Removed previous route");
        }
        
        // Show loading message
        Toast.makeText(requireContext(), "Đang tính toán đường đi...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Requesting directions from DirectionsHelper...");
        
        directionsHelper.getDirections(origin, destination, new DirectionsHelper.DirectionsCallback() {
            @Override
            public void onSuccess(List<LatLng> routePoints, String distance, String duration) {
                Log.d(TAG, "Directions API success - routePoints size: " + 
                      (routePoints != null ? routePoints.size() : 0));
                
                if (googleMap == null) {
                    Log.e(TAG, "GoogleMap is null - cannot draw route");
                    return;
                }
                
                if (routePoints == null || routePoints.isEmpty()) {
                    Log.e(TAG, "Route points are null or empty");
                    Toast.makeText(requireContext(), "Không có đường đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                try {
                    // Draw polyline with cyan/light blue color (xanh nước biển)
                    // Using a vibrant cyan-blue color (xanh nước biển)
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(routePoints)
                            .color(0xFF00ACC1) // Cyan (xanh nước biển) - vibrant and visible
                            .width(12f); // Thicker line for better visibility
                    
                    currentRoute = googleMap.addPolyline(polylineOptions);
                    
                    if (currentRoute != null) {
                        Log.d(TAG, "Polyline added successfully with " + routePoints.size() + " points");
                    } else {
                        Log.e(TAG, "Failed to add polyline to map");
                    }
                    
                    // Fit camera to show both origin and destination
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(origin);
                    builder.include(destination);
                    
                    // Include all route points for better view
                    for (LatLng point : routePoints) {
                        builder.include(point);
                    }
                    
                    LatLngBounds bounds = builder.build();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150)); // 150dp padding
                    
                    // Show distance and duration
                    Toast.makeText(requireContext(), 
                        "Khoảng cách: " + distance + " | Thời gian: " + duration, 
                        Toast.LENGTH_LONG).show();
                    
                    Log.d(TAG, "Route drawn successfully: " + routePoints.size() + " points, " + 
                          distance + ", " + duration);
                } catch (Exception e) {
                    Log.e(TAG, "Error drawing route", e);
                    Toast.makeText(requireContext(), "Lỗi vẽ đường đi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Directions API failure: " + errorMessage);
                Log.d(TAG, "Falling back to simple route drawing");
                
                // Backup: Draw simple straight line route
                drawSimpleRoute(origin, destination);
                
                Toast.makeText(requireContext(), "Đang hiển thị đường đi đơn giản", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Backup method: Draw a simple route (straight line or interpolated points) 
     * when Directions API is not available
     */
    private void drawSimpleRoute(LatLng origin, LatLng destination) {
        if (googleMap == null) {
            Log.e(TAG, "GoogleMap is null - cannot draw simple route");
            return;
        }
        
        // Remove previous route if exists
        if (currentRoute != null) {
            currentRoute.remove();
        }
        
        try {
            // Create interpolated points for a smoother line (not just straight)
            List<LatLng> routePoints = createInterpolatedRoute(origin, destination, 20);
            
            // Draw polyline with cyan color (xanh nước biển)
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .color(0xFF00ACC1) // Cyan (xanh nước biển)
                    .width(12f)
                    .geodesic(true); // Follow Earth's curvature
            
            currentRoute = googleMap.addPolyline(polylineOptions);
            
            // Calculate approximate distance
            double distance = calculateDistance(origin, destination);
            String distanceText = formatDistance(distance);
            
            // Fit camera to show both origin and destination
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
            
            Toast.makeText(requireContext(), 
                "Khoảng cách: " + distanceText, 
                Toast.LENGTH_LONG).show();
            
            Log.d(TAG, "Simple route drawn successfully - distance: " + distanceText);
        } catch (Exception e) {
            Log.e(TAG, "Error drawing simple route", e);
            Toast.makeText(requireContext(), "Lỗi vẽ đường đi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Create interpolated points between origin and destination for smoother line
     */
    private List<LatLng> createInterpolatedRoute(LatLng origin, LatLng destination, int numPoints) {
        List<LatLng> points = new ArrayList<>();
        points.add(origin);
        
        for (int i = 1; i < numPoints; i++) {
            double fraction = (double) i / numPoints;
            double lat = origin.latitude + (destination.latitude - origin.latitude) * fraction;
            double lng = origin.longitude + (destination.longitude - origin.longitude) * fraction;
            points.add(new LatLng(lat, lng));
        }
        
        points.add(destination);
        return points;
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(LatLng point1, LatLng point2) {
        final int R = 6371; // Earth's radius in kilometers
        
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double deltaLat = Math.toRadians(point2.latitude - point1.latitude);
        double deltaLng = Math.toRadians(point2.longitude - point1.longitude);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in kilometers
    }
    
    /**
     * Format distance for display
     */
    private String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) (distanceKm * 1000);
            return meters + " m";
        } else {
            return String.format("%.1f km", distanceKm);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (googleMap != null) {
                    if (ContextCompat.checkSelfPermission(requireContext(), 
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                }
                Toast.makeText(requireContext(), "Đã cấp quyền vị trí", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Cần quyền vị trí để hiển thị vị trí hiện tại", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}