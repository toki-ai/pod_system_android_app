package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignmentpod.R;
import com.example.assignmentpod.data.repository.CartRepository;
import com.example.assignmentpod.model.room.RoomType;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.RoomTypeViewHolder> {
    
    private List<RoomType> roomTypes = new ArrayList<>();
    private OnRoomTypeClickListener listener;
    private final CartRepository cartRepository;
    private final LifecycleOwner lifecycleOwner;
    
    public RoomTypeAdapter(CartRepository cartRepository, LifecycleOwner lifecycleOwner) {
        this.cartRepository = cartRepository;
        this.lifecycleOwner = lifecycleOwner;
    }

    public interface OnRoomTypeClickListener {
        void onRoomTypeClick(RoomType roomType);
        void onBookClick(RoomType roomType);
        void onAddToCartClick(RoomType roomType);
        void onRemoveFromCartClick(RoomType roomType);
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
//        private MaterialButton btnBook;
        private MaterialButton btnAddToCart;
        private Boolean currentIsInCart = null;

        public RoomTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.iv_room_image);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvRoomDescription = itemView.findViewById(R.id.tv_room_description);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvAvailable = itemView.findViewById(R.id.tv_available);
            tvPrice = itemView.findViewById(R.id.tv_price);
//            btnBook = itemView.findViewById(R.id.btn_book);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
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
            
            // Load room image using Glide
            if (roomType.getImage() != null && !roomType.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(roomType.getImage())
                    .placeholder(R.drawable.placeholder_room)
                    .error(R.drawable.placeholder_room)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivRoomImage);
            } else {
                // Use placeholder if no image URL
                ivRoomImage.setImageResource(R.drawable.placeholder_room);
            }
            
            // Enable/disable book button based on availability
//            btnBook.setEnabled(available > 0);
//            btnBook.setText(available > 0 ? "ĐẶT PHÒNG" : "HẾT PHÒNG");
            
            // Reflect current cart status on star icon (one-time init per bind)
            if (cartRepository != null && lifecycleOwner != null && btnAddToCart != null) {
                cartRepository.isRoomInCart(roomType.getId()).observe(lifecycleOwner, isInCart -> {
                    currentIsInCart = isInCart != null && isInCart;
                    updateStarIcon(currentIsInCart);
                });
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRoomTypeClick(roomType);
                }
            });
            
//            btnBook.setOnClickListener(v -> {
//                if (listener != null && available > 0) {
//                    listener.onBookClick(roomType);
//                }
//            });

            if (btnAddToCart != null) {
                btnAddToCart.setEnabled(available > 0);
                btnAddToCart.setOnClickListener(v -> {
                    if (listener == null || available <= 0) return;

                    // Simple debounce to avoid double taps
                    btnAddToCart.setEnabled(false);
                    btnAddToCart.postDelayed(() -> btnAddToCart.setEnabled(true), 300);

                    boolean isInCartNow = currentIsInCart != null && currentIsInCart;
                    if (isInCartNow) {
                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Xóa khỏi giỏ hàng")
                                .setMessage("Bạn có chắc muốn bỏ \"" + roomType.getName() + "\" khỏi giỏ hàng?")
                                .setPositiveButton("Bỏ khỏi giỏ", (dialog, which) -> {
                                    currentIsInCart = false;
                                    updateStarIcon(false);
                                    listener.onRemoveFromCartClick(roomType);
                                })
                                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        currentIsInCart = true;
                        updateStarIcon(true);
                        listener.onAddToCartClick(roomType);
                    }
                });
            }
        }

        private void updateStarIcon(boolean isInCart) {
            if (btnAddToCart == null) return;

            if (isInCart) {
                btnAddToCart.setIconResource(R.drawable.heart);
                btnAddToCart.setIconTintResource(R.color.error);
            } else {
                btnAddToCart.setIconResource(R.drawable.heart_out_line);
            }
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