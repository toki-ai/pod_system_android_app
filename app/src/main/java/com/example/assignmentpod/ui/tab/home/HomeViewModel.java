package com.example.assignmentpod.ui.tab.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.repository.RoomTypeRepository;
import com.example.assignmentpod.data.repository.UserRepository;
import com.example.assignmentpod.model.response.PaginationResponse;
import com.example.assignmentpod.model.room.RoomType;
import com.example.assignmentpod.model.user.UserProfile;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";

    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    
    // LiveData
    private final MutableLiveData<List<RoomType>> roomTypesLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    public HomeViewModel(@NonNull Application application) {
        super(application);
        roomTypeRepository = new RoomTypeRepository();
        userRepository = new UserRepository(application);
    }

    public MutableLiveData<List<RoomType>> getRoomTypesLiveData() {
        return roomTypesLiveData;
    }
    
    public MutableLiveData<UserProfile> getUserProfileLiveData() {
        return userProfileLiveData;
    }
    
    public MutableLiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }
    
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public interface LoadMoreCallback {
        void onSuccess(PaginationResponse<List<RoomType>> response, List<RoomType> newRoomTypes);
        void onError(String error);
    }

    public void loadRoomTypes() {
        Log.d(TAG, "Loading room types...");
        isLoadingLiveData.setValue(true);
        
        roomTypeRepository.getRoomTypes(1, 10, new RoomTypeRepository.RoomTypesCallback() {
            @Override
            public void onSuccess(PaginationResponse<List<RoomType>> response) {
                isLoadingLiveData.postValue(false);
                
                if (response.getData() != null) {
                    roomTypesLiveData.postValue(response.getData());
                    Log.d(TAG, "Loaded " + response.getData().size() + " room types");
                } else {
                    roomTypesLiveData.postValue(null);
                    Log.w(TAG, "Room types response data is null");
                }
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue("Failed to load room types: " + error);
                Log.e(TAG, "Error loading room types: " + error);
            }
        });
    }

    public void loadMoreRoomTypes(int page, int take, LoadMoreCallback callback) {
        Log.d(TAG, "Loading more room types for page " + page);
        
        roomTypeRepository.getRoomTypes(page, take, new RoomTypeRepository.RoomTypesCallback() {
            @Override
            public void onSuccess(PaginationResponse<List<RoomType>> response) {
                if (response.getData() != null) {
                    callback.onSuccess(response, response.getData());
                    Log.d(TAG, "Loaded " + response.getData().size() + " more room types");
                } else {
                    callback.onError("No data in response");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to load more room types: " + error);
                Log.e(TAG, "Error loading more room types: " + error);
            }
        });
    }

    public void loadUserProfile() {
        Log.d(TAG, "HomeViewModel: Starting loadUserProfile...");
        
        userRepository.getUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                Log.d(TAG, "HomeViewModel: Profile loaded successfully - " + userProfile.getName());
                userProfileLiveData.postValue(userProfile);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "HomeViewModel: Failed to load user profile - " + error);
                userProfileLiveData.postValue(null);
            }
        });
    }

    public void refreshData() {
        loadRoomTypes();
        loadUserProfile();
    }
}