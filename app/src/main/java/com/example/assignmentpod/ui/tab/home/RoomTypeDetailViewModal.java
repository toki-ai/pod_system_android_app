package com.example.assignmentpod.ui.tab.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.assignmentpod.data.repository.RoomRepository;
import com.example.assignmentpod.data.repository.RoomTypeRepository;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.room.RoomType;
import com.example.assignmentpod.model.slot.SlotDTO;

import java.util.Collections;
import java.util.List;

public class RoomTypeDetailViewModal extends AndroidViewModel {
    private static final String TAG = "RoomTypeDetailViewModal";

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<RoomType> roomTypeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<List<SlotDTO>> availableSlotsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Room>> availableRoomsLiveData = new MutableLiveData<>();


    public RoomTypeDetailViewModal(@NonNull Application application) {
        super(application);
        roomTypeRepository = new RoomTypeRepository();
        roomRepository = new RoomRepository();
    }

    // Getters
    public MutableLiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
    public MutableLiveData<RoomType> getRoomTypeLiveData() { return roomTypeLiveData; }
    public MutableLiveData<List<Room>> getAvailableRoomsLiveData() { return availableRoomsLiveData; }
    public MutableLiveData<List<SlotDTO>> getAvailableSlotsLiveData() { return availableSlotsLiveData; }

    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
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


    public void loadAvailableRoomsByTypeAndDate(int roomTypeId, String date) {
        Log.d(TAG, "Loading available rooms by typeId=" + roomTypeId + " and date=" + date);
        roomRepository.getAvailableRoomsByTypeAndDate(roomTypeId, date, new RoomRepository.RoomListCallback() {
            @Override
            public void onSuccess(List<Room> rooms) {
                isLoadingLiveData.postValue(false);
                availableRoomsLiveData.postValue(rooms);
                Log.d(TAG, "Fetched " + (rooms != null ? rooms.size() : 0) + " rooms for date: " + date);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                availableRoomsLiveData.postValue(Collections.emptyList());
                errorLiveData.postValue("Failed to load available rooms by date: " + error);
                Log.e(TAG, "Error fetching available rooms by date: " + error);
            }
        });
    }

    public void loadSlotsByRoomsAndDate(List<Integer> roomIds, String date) {
        Log.d(TAG, "Loading slots by roomIds=" + roomIds + " and date=" + date);
        roomRepository.getSlotsByRoomsAndDate(roomIds, date, new RoomRepository.SlotListCallback() {
            @Override
            public void onSuccess(List<SlotDTO> slots) {
                isLoadingLiveData.postValue(false);
                availableSlotsLiveData.postValue(slots);
                Log.d(TAG, "Fetched " + (slots != null ? slots.size() : 0) + " slots for date: " + date);
            }

            @Override
            public void onError(String error) {
                isLoadingLiveData.postValue(false);
                availableSlotsLiveData.postValue(Collections.emptyList());
                errorLiveData.postValue("Failed to load slots: " + error);
                Log.e(TAG, "Error fetching slots: " + error);
            }
        });
    }
}
