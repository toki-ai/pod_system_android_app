package com.example.assignmentpod.ui.tab.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RoomTypeDetailFragment extends Fragment {
    private static final String TAG = "RoomTypeDetailFragment";

    private RoomTypeDetailViewModal viewModal;
    private NavController navController;
    private TextView  tvRoomName, tvPrice, tvDiscount, tvTotalPrice;
    private ImageView imgMain;
    private Button btnAddToCart, btnBook;
    private EditText etDate;
    private Spinner spSlot, spRoom, spPackage;

    private int productId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModal = new ViewModelProvider(this).get(RoomTypeDetailViewModal.class);
        Bundle args = getArguments();
        if (args != null) {
            productId = args.getInt("roomTypeId", 0);
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
            viewModal.loadRoomTypeDetail(productId);

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
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModal() {
        viewModal.getRoomTypeLiveData().observe(getViewLifecycleOwner(), roomType -> {
           if (roomType != null) {
               tvRoomName.setText(roomType.getName());
               tvPrice.setText(roomType.getPrice() + " VND/tiếng");
//               tvDiscount.setText("0");
               tvTotalPrice.setText(roomType.getPrice() + " VND");
               //TODO: cần tính discount, total price và set ảnh
//               imgMain.setImageURI(roomType.getImage());
           }
        });
    }

}