package com.example.assignmentpod.ui.tab.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.slot.SlotDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RoomTypeDetailFragment extends Fragment {
    private static final String TAG = "RoomTypeDetailFragment";

    private RoomTypeDetailViewModal viewModal;
    private NavController navController;
    private TextView tvRoomName, tvPrice, tvDiscount, tvTotalPrice;
    private ImageView imgMain;
    private Button btnAddToCart, btnBook;
    private EditText etDate;
    private Spinner spSlot, spRoom, spPackage;

    private int roomTypeId;
    private final String[] SLOT_ARRAY = {
            "07:00 - 09:00", "09:00 - 11:00", "11:00 - 13:00",
            "13:00 - 15:00", "15:00 - 17:00", "17:00 - 19:00", "19:00 - 21:00"
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModal = new ViewModelProvider(this).get(RoomTypeDetailViewModal.class);
        Bundle args = getArguments();
        if (args != null) {
            roomTypeId = args.getInt("roomTypeId", 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_room_type_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        try {
            initViews(view);
            observeViewModal();
            viewModal.loadRoomTypeDetail(roomTypeId);

            // ✅ Default date = today
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(calendar.getTime());
            etDate.setText(today);
            viewModal.setSelectedDate(today);

            // ✅ Load room type + available rooms immediately
            viewModal.loadRoomTypeDetail(roomTypeId);
            viewModal.loadAvailableRoomsByTypeAndDate(roomTypeId, today);

            // ✅ DatePicker -> auto refresh rooms when user changes date
            etDate.setOnClickListener(v -> {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(requireContext(), (DatePicker dp, int year, int month, int day) -> {
                    String selected = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
                    etDate.setText(selected);
                    viewModal.setSelectedDate(selected);
                    viewModal.loadAvailableRoomsByTypeAndDate(roomTypeId, selected);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            });


            Log.d(TAG, "RoomTypeDetailFragment setup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
        }
    }

    private void initViews(View view) {
        tvRoomName = view.findViewById(R.id.tv_room_type_detail_name);
        tvPrice = view.findViewById(R.id.tv_room_type_detail_price);
        tvDiscount = view.findViewById(R.id.tv_room_type_detail_discount);
        imgMain = view.findViewById(R.id.tv_room_type_detail_img_main);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        btnBook = view.findViewById(R.id.btn_book_room_type_detail);
        etDate = view.findViewById(R.id.et_room_type_detail_date);
        tvTotalPrice = view.findViewById(R.id.tv_room_type_detail_total_price);
        spRoom = view.findViewById(R.id.spRoom);
        spSlot = view.findViewById(R.id.spSlot);
        spPackage = view.findViewById(R.id.spPackage);
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModal() {
        // ✅ Observe Room Type
        viewModal.getRoomTypeLiveData().observe(getViewLifecycleOwner(), roomType -> {
            if (roomType != null) {
                tvRoomName.setText(roomType.getName());
                tvPrice.setText(roomType.getPrice() + " VND/giờ");
                tvTotalPrice.setText(roomType.getPrice() + " VND");
                // TODO: Add discount + image if needed
            }
        });

        // ✅ Observe Available Rooms
        viewModal.getAvailableRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                spRoom.setEnabled(true);

                List<String> roomNames = rooms.stream()
                        .map(Room::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        roomNames
                );
                spRoom.setAdapter(roomAdapter);

                // ✅ Load slots for selected date and rooms
                viewModal.loadSlotsByRoomsAndDate(
                        rooms.stream().map(Room::getId).collect(Collectors.toList()),
                        etDate.getText().toString()
                );
            } else {
                spRoom.setEnabled(false);
                spSlot.setEnabled(false);
                spSlot.setAdapter(null);
            }
        });

        // ✅ Observe Available Slots
        viewModal.getAvailableSlotsLiveData().observe(getViewLifecycleOwner(), slots -> {
            if (slots != null && !slots.isEmpty()) {
                List<String> slotLabels = slots.stream()
                        .map(s -> String.format("%s - %s",
                                s.getStartTime().toLocalTime(),
                                s.getEndTime().toLocalTime()))
                        .collect(Collectors.toList());

                ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        slotLabels
                );
                spSlot.setAdapter(slotAdapter);
                spSlot.setEnabled(true);
            } else {
                spSlot.setEnabled(false);
                spSlot.setAdapter(null);
            }
        });
    }

}