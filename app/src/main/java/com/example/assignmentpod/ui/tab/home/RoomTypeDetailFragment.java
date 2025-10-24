package com.example.assignmentpod.ui.tab.home;

import static com.example.assignmentpod.utils.Utils.convertDisplayDateToApi;
import static com.example.assignmentpod.utils.Utils.formatPrice;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.room.Room;
import com.example.assignmentpod.model.servicepackage.ServicePackage;
import com.example.assignmentpod.model.slot.Slot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
            "07:00 - 09:00",
            "09:00 - 11:00",
            "11:00 - 13:00",
            "13:00 - 15:00",
            "15:00 - 17:00",
            "17:00 - 19:00",
            "19:00 - 21:00"
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
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            String todayDisplay = displayFormat.format(calendar.getTime()); // display on UI as dd-MM-yyyy
            String todayApi = apiFormat.format(calendar.getTime()); // api as yyyy-MM-dd

            etDate.setText(todayDisplay);
            viewModal.setSelectedDate(todayApi);

            // ✅ Load room type + available rooms + service packages immediately
            viewModal.loadRoomTypeDetail(roomTypeId);
            viewModal.loadAvailableRoomsByTypeAndDate(roomTypeId, todayApi);
            viewModal.loadAllServicePackages();

            // ✅ DatePicker -> auto refresh rooms when user changes date
            etDate.setOnClickListener(v -> {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(requireContext(), (DatePicker dp, int year, int month, int day) -> {
                    String displayDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year);
                    String apiDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);

                    etDate.setText(displayDate);
                    viewModal.setSelectedDate(apiDate);
                    viewModal.loadAvailableRoomsByTypeAndDate(roomTypeId, apiDate);
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

                tvPrice.setText(formatPrice(roomType.getPrice()) + " /giờ");
                tvTotalPrice.setText(formatPrice(roomType.getPrice()));
                updateDiscountAndTotal();
                // TODO: Image if needed
            }
        });

        // ✅ Observe Available Rooms
        viewModal.getAvailableRoomsLiveData().observe(getViewLifecycleOwner(), rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                spRoom.setEnabled(true);
                spRoom.setPrompt("Chọn phòng");

                List<String> roomNames = rooms.stream()
                        .map(Room::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        roomNames
                );
                roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spRoom.setAdapter(roomAdapter);

                // ✅ Load slots for selected date and rooms
                viewModal.loadSlotsByRoomsAndDate(
                        rooms.stream().map(Room::getId).collect(Collectors.toList()),
                        convertDisplayDateToApi(TAG, etDate.getText().toString()));
            } else {
                spRoom.setEnabled(false);
                spSlot.setEnabled(false);
                spSlot.setAdapter(null);
            }
        });

        // ✅ Observe Available Slots
        viewModal.getAvailableSlotsLiveData().observe(getViewLifecycleOwner(), slots -> {
            if (slots != null && !slots.isEmpty()) {
                spSlot.setPrompt("Chọn khung giờ");
                List<String> availableSlotLabels = filterAvailableSlots(slots);

                if (!availableSlotLabels.isEmpty()) {
                    ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            availableSlotLabels
                    );
                    slotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSlot.setAdapter(slotAdapter);
                    spSlot.setEnabled(true);
                }
            } else {
                spSlot.setEnabled(false);
                spSlot.setAdapter(null);
            }
        });

        // ✅ Observe Service Packages
        viewModal.getServicePackagesLiveData().observe(getViewLifecycleOwner(), servicePackages -> {
            if (servicePackages != null && !servicePackages.isEmpty()) {
                spPackage.setPrompt("Chọn gói dịch vụ");
                List<String> servicePackageLabels = servicePackages.stream()
                        .map(ServicePackage::getName)
                        .collect(Collectors.toList());

                ArrayAdapter<String> servicePackageAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        servicePackageLabels
                );
                servicePackageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPackage.setAdapter(servicePackageAdapter);
                spPackage.setEnabled(true);

                // ✅ When user selects a package → update discount and total
                spPackage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                        updateDiscountAndTotal();
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                });
            } else {
                spPackage.setEnabled(false);
                spPackage.setAdapter(null);
            }
        });
    }

    private void updateDiscountAndTotal() {
        // Defensive checks to avoid NPE
        if (viewModal.getServicePackagesLiveData().getValue() == null ||
                viewModal.getServicePackagesLiveData().getValue().isEmpty() ||
                viewModal.getRoomTypeLiveData().getValue() == null ||
                spPackage.getSelectedItemPosition() == android.widget.AdapterView.INVALID_POSITION) {
            return;
        }

        List<ServicePackage> packages = viewModal.getServicePackagesLiveData().getValue();
        int pos = spPackage.getSelectedItemPosition();

        // Extra safety
        if (pos < 0 || pos >= packages.size()) return;

        ServicePackage selectedPackage = packages.get(pos);
        int price = viewModal.getRoomTypeLiveData().getValue().getPrice();
        int discountPercent = selectedPackage.getDiscountPercentage();

        double discountedPrice = price * (1 - discountPercent / 100.0);

        tvDiscount.setText(String.format(Locale.getDefault(), "-%d%%", discountPercent));
        tvTotalPrice.setText(formatPrice(discountedPrice));
    }


    private List<String> filterAvailableSlots(List<Slot> availableSlots) {
        LocalTime now = LocalTime.now();

        // Convert available Slot objects to "HH:mm - HH:mm" strings
        List<String> available = availableSlots.stream()
                .map(s -> String.format("%s - %s",
                        s.getStartTime().toLocalTime().withSecond(0).withNano(0),
                        s.getEndTime().toLocalTime().withSecond(0).withNano(0)))
                .collect(Collectors.toList());

        // Keep only slots that are defined in SLOT_ARRAY AND not ended yet
        return Arrays.stream(SLOT_ARRAY)
                .filter(available::contains)
                .filter(slot -> {
                    String[] parts = slot.split(" - ");
                    LocalTime end = LocalTime.parse(parts[1]);
                    return end.isAfter(now); // Only keep ongoing or future slots
                })
                .collect(Collectors.toList());
    }

}