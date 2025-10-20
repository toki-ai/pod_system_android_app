package com.example.assignmentpod.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.cart.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private OnCartItemClickListener listener;
    
    public interface OnCartItemClickListener {
        void onItemClick(CartItem cartItem);
        void onDeleteClick(CartItem cartItem);
    }
    
    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.listener = listener;
    }
    
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }
    
    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    
    class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName;
        private TextView roomDescription;
        private TextView roomType;
        private TextView roomPrice;
        private ImageView roomImage;
        private ImageView deleteButton;
        
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tv_room_name);
            roomDescription = itemView.findViewById(R.id.tv_room_description);
            roomType = itemView.findViewById(R.id.tv_room_type);
            roomPrice = itemView.findViewById(R.id.tv_room_price);
            roomImage = itemView.findViewById(R.id.iv_room_image);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(cartItems.get(position));
                    }
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(cartItems.get(position));
                    }
                }
            });
        }
        
        public void bind(CartItem cartItem) {
            roomName.setText(cartItem.getRoomName());
            roomDescription.setText(cartItem.getRoomDescription());
            roomType.setText(cartItem.getRoomTypeName());
            roomPrice.setText(String.format("%,.0f VND", cartItem.getRoomPrice()));
            
            // Set placeholder image for now
            roomImage.setImageResource(R.drawable.placeholder_room);
        }
    }
}
