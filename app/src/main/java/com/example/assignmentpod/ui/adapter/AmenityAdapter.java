package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.order.Amenity;
import com.example.assignmentpod.utils.CurrencyFormatter;

import java.util.List;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder> {
    private List<Amenity> amenities;

    public AmenityAdapter(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    @NonNull
    @Override
    public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AmenityViewHolder(
                (android.view.View) inflater.inflate(R.layout.item_amenity, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
        Amenity amenity = amenities.get(position);
        holder.bind(amenity);
    }

    @Override
    public int getItemCount() {
        return amenities != null ? amenities.size() : 0;
    }

    public void updateData(List<Amenity> newAmenities) {
        this.amenities = newAmenities;
        notifyDataSetChanged();
    }

    public static class AmenityViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAmenityName;
        private final TextView tvAmenityType;
        private final TextView tvAmenityQuantity;
        private final TextView tvAmenityUnitPrice;
        private final TextView tvAmenityTotalPrice;

        public AmenityViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvAmenityName = itemView.findViewById(R.id.tv_amenity_name);
            tvAmenityType = itemView.findViewById(R.id.tv_amenity_type);
            tvAmenityQuantity = itemView.findViewById(R.id.tv_amenity_quantity);
            tvAmenityUnitPrice = itemView.findViewById(R.id.tv_amenity_unit_price);
            tvAmenityTotalPrice = itemView.findViewById(R.id.tv_amenity_total_price);
        }

        public void bind(Amenity amenity) {
            tvAmenityName.setText(amenity.getAmenityName());
            tvAmenityType.setText(amenity.getAmenityType());
            tvAmenityQuantity.setText(String.valueOf(amenity.getQuantity()));
            tvAmenityUnitPrice.setText(CurrencyFormatter.formatAmount(amenity.getUnitPrice()));
            tvAmenityTotalPrice.setText(CurrencyFormatter.formatAmount(amenity.getTotalPrice()));
        }
    }
}
