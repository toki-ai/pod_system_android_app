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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private LocationHelper locationHelper;
    private List<Building> buildings = new ArrayList<>();
    private Building selectedBuilding;
    
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

        // Add building markers
        addBuildingMarkers();

        // Center map on Ho Chi Minh City
        LatLng hcmcCenter = new LatLng(10.7769, 106.7009);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmcCenter, 12));
        Log.d(TAG, "Map camera moved to HCMC center");
        
        // Debug: Check map type
        Log.d(TAG, "Map type: " + googleMap.getMapType());
        Log.d(TAG, "Map is ready - UI settings: " + googleMap.getUiSettings());
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
        Building building = (Building) marker.getTag();
        if (building != null) {
            selectedBuilding = building;
            showBuildingInfo(building);
            
            // Move camera to marker
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            
            return true; // Consume the event
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