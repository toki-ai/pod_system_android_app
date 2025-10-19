package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.room.RoomType;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.RoomTypeViewHolder> {
    
    private List<RoomType> roomTypes = new ArrayList<>();
    private OnRoomTypeClickListener listener;
    
    public interface OnRoomTypeClickListener {
        void onRoomTypeClick(RoomType roomType);
        void onBookClick(RoomType roomType);
    }
    
    public void setOnRoomTypeClickListener(OnRoomTypeClickListener listener) {
        this.listener = listener;
    }
    
    public void setRoomTypes(List<RoomType> roomTypes) {
        this.roomTypes = roomTypes != null ? roomTypes : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addRoomTypes(List<RoomType> newRoomTypes) {
        if (newRoomTypes != null) {
            int startPosition = this.roomTypes.size();
            this.roomTypes.addAll(newRoomTypes);
            notifyItemRangeInserted(startPosition, newRoomTypes.size());
        }
    }
    
    public void clearRoomTypes() {
        this.roomTypes.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_type, parent, false);
        return new RoomTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTypeViewHolder holder, int position) {
        RoomType roomType = roomTypes.get(position);
        holder.bind(roomType);
    }

    @Override
    public int getItemCount() {
        return roomTypes.size();
    }

    class RoomTypeViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivRoomImage;
        private TextView tvRoomName;
        private TextView tvRoomDescription;
        private TextView tvCapacity;
        private TextView tvAvailable;
        private TextView tvPrice;
        private MaterialButton btnBook;

        public RoomTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.iv_room_image);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvRoomDescription = itemView.findViewById(R.id.tv_room_description);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvAvailable = itemView.findViewById(R.id.tv_available);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnBook = itemView.findViewById(R.id.btn_book);
        }

        public void bind(RoomType roomType) {
            // Set room name
            tvRoomName.setText(roomType.getName());
            
            // Set description based on room type name
            String description = generateDescription(roomType.getName());
            tvRoomDescription.setText(description);
            
            // Set capacity
            tvCapacity.setText("👤 " + roomType.getCapacity() + " người");
            
            // Set available rooms
            int available = roomType.getQuantity();
            String availableText = available > 0 ? "🟢 " + available + " phòng" : "🔴 Hết phòng";
            tvAvailable.setText(availableText);
            
            // Set price
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String priceText = formatter.format(roomType.getPrice()).replace("₫", "VND");
            tvPrice.setText(priceText);
            
            // Set room image (placeholder for now)
            ivRoomImage.setImageResource(R.drawable.placeholder_room);
            
            // Enable/disable book button based on availability
            btnBook.setEnabled(available > 0);
            btnBook.setText(available > 0 ? "ĐẶT PHÒNG" : "HẾT PHÒNG");
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRoomTypeClick(roomType);
                }
            });
            
            btnBook.setOnClickListener(v -> {
                if (listener != null && available > 0) {
                    listener.onBookClick(roomType);
                }
            });
        }
        
        private String generateDescription(String roomTypeName) {
            if (roomTypeName == null) {
                return "Tư nhân, riêng tư";
            }
            
            String name = roomTypeName.toLowerCase();
            if (name.contains("single") || name.contains("1")) {
                return "Tư nhân, riêng tư";
            } else if (name.contains("double") || name.contains("2")) {
                return "Không gian làm việc cho 2 người";
            } else if (name.contains("meeting")) {
                return "Phòng họp chuyên nghiệp";
            } else if (name.contains("conference")) {
                return "Phòng hội nghị lớn";
            } else {
                return "Không gian làm việc hiện đại";
            }
        }
    }
}