package com.example.assignmentpod.ui.tab.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.repository.RoomTypeRepository;
import com.example.assignmentpod.model.room.RoomType;

public class RoomTypeDetailViewModal extends AndroidViewModel {
    private static final String TAG = "RoomTypeDetailViewModal";

    private final RoomTypeRepository roomTypeRepository;

    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<RoomType> roomTypeLiveData = new MutableLiveData<>();

    public RoomTypeDetailViewModal(@NonNull Application application) {
        super(application);
        roomTypeRepository = new RoomTypeRepository();
    }

    public MutableLiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public MutableLiveData<RoomType> getRoomTypeLiveData() {
        return roomTypeLiveData;
    }

    public void loadRoomTypeDetail(int roomTypeId) {
        Log.d(TAG, "Loading room type detail for ID: " + roomTypeId);
        roomTypeRepository.getRoomTypeById(roomTypeId, new RoomTypeRepository.RoomTypeCallback() {
            @Override
            public void onSuccess(RoomType roomType) {
                isLoadingLiveData.postValue(false);
                if (roomType != null) {
                    roomTypeLiveData.postValue(roomType);
                    Log.d(TAG, "Room type detail loaded successfully: " + roomType.getName());
                } else {
                    roomTypeLiveData.postValue(null);
                    Log.w(TAG, "Room type detail response data is null");
                }
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue("Failed to load room type detail: " + error);
                Log.e(TAG, "Error loading room type detail: " + error);
            }
        });
    }

}
