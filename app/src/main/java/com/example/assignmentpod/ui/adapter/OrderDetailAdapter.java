package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.order.Amenity;
import com.example.assignmentpod.model.order.OrderDetail;
import com.example.assignmentpod.utils.CurrencyFormatter;
import com.example.assignmentpod.utils.DateTimeFormatterUtil;
import com.example.assignmentpod.utils.OrderStatusMapper;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetail> orderDetails;

    public OrderDetailAdapter(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new OrderDetailViewHolder(
                (android.view.View) inflater.inflate(R.layout.item_order_detail, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail detail = orderDetails.get(position);
        holder.bind(detail);
    }

    @Override
    public int getItemCount() {
        return orderDetails != null ? orderDetails.size() : 0;
    }

    public void updateData(List<OrderDetail> newDetails) {
        this.orderDetails = newDetails;
        notifyDataSetChanged();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivRoomImage;
        private final TextView tvRoomName;
        private final TextView tvBuildingAddress;
        private final TextView tvTimeRange;
        private final TextView tvDetailStatus;
        private final TextView tvServicePackage;
        private final LinearLayout layoutAmenitiesSection;
        private final RecyclerView recyclerViewAmenities;
        private final LinearLayout layoutAmenitiesTotal;
        private final TextView tvAmenitiesTotal;
        private final LinearLayout layoutDiscount;
        private final TextView tvDiscount;
        private final TextView tvRoomCharge;
        private final TextView tvSubtotal;
        private final TextView tvTotalPrice;

        public OrderDetailViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            ivRoomImage = itemView.findViewById(R.id.iv_room_image);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            tvBuildingAddress = itemView.findViewById(R.id.tv_building_address);
            tvTimeRange = itemView.findViewById(R.id.tv_time_range);
            tvDetailStatus = itemView.findViewById(R.id.tv_detail_status);
            tvServicePackage = itemView.findViewById(R.id.tv_service_package);
            layoutAmenitiesSection = itemView.findViewById(R.id.layout_amenities_section);
            recyclerViewAmenities = itemView.findViewById(R.id.recycler_view_amenities);
            layoutAmenitiesTotal = itemView.findViewById(R.id.layout_amenities_total);
            tvAmenitiesTotal = itemView.findViewById(R.id.tv_amenities_total);
            layoutDiscount = itemView.findViewById(R.id.layout_discount);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvRoomCharge = itemView.findViewById(R.id.tv_room_charge);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
        }

        public void bind(OrderDetail detail) {
            // Room Info
            tvRoomName.setText(detail.getRoomName());
            tvBuildingAddress.setText(detail.getBuildingAddress());
            tvTimeRange.setText(DateTimeFormatterUtil.formatTimeRange(detail.getStartTime(), detail.getEndTime()) 
                    + " (" + DateTimeFormatterUtil.calculateDuration(detail.getStartTime(), detail.getEndTime()) + ")");
            tvDetailStatus.setText(OrderStatusMapper.getDisplayStatus(detail.getStatus()));
            tvDetailStatus.setBackgroundColor(OrderStatusMapper.getStatusColor(detail.getStatus()));
            tvServicePackage.setText(detail.getServicePackageName());

            // Amenities
            List<Amenity> amenities = detail.getAmenities();
            if (amenities != null && !amenities.isEmpty()) {
                layoutAmenitiesSection.setVisibility(android.view.View.VISIBLE);
                layoutAmenitiesTotal.setVisibility(android.view.View.VISIBLE);
                
                AmenityAdapter amenityAdapter = new AmenityAdapter(amenities);
                recyclerViewAmenities.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                recyclerViewAmenities.setAdapter(amenityAdapter);

                // Calculate amenities total
                double amenitiesTotal = amenities.stream()
                        .mapToDouble(Amenity::getTotalPrice)
                        .sum();
                tvAmenitiesTotal.setText(CurrencyFormatter.formatVND(amenitiesTotal));
            } else {
                layoutAmenitiesSection.setVisibility(android.view.View.GONE);
                layoutAmenitiesTotal.setVisibility(android.view.View.GONE);
            }

            // Billing
            double roomPrice = detail.getRoomPrice() > 0 ? detail.getRoomPrice() : detail.getPriceRoom();
            tvRoomCharge.setText(CurrencyFormatter.formatVND(roomPrice));

            if (detail.getDiscountPercentage() > 0) {
                layoutDiscount.setVisibility(android.view.View.VISIBLE);
                double discountAmount = CurrencyFormatter.calculateDiscount(roomPrice, detail.getDiscountPercentage());
                tvDiscount.setText("-" + CurrencyFormatter.formatVND(discountAmount));
            } else {
                layoutDiscount.setVisibility(android.view.View.GONE);
            }

            // Subtotal and Total
            if (detail.getBillingBreakdown() != null) {
                tvSubtotal.setText(CurrencyFormatter.formatVND(detail.getBillingBreakdown().getSubtotal()));
                tvTotalPrice.setText(CurrencyFormatter.formatVND(detail.getBillingBreakdown().getTotalAmount()));
            } else {
                // Calculate manually if billing breakdown not provided
                double amenitiesTotal = 0;
                if (amenities != null && !amenities.isEmpty()) {
                    amenitiesTotal = amenities.stream()
                            .mapToDouble(Amenity::getTotalPrice)
                            .sum();
                }
                double subtotal = roomPrice + amenitiesTotal;
                tvSubtotal.setText(CurrencyFormatter.formatVND(subtotal));
                
                double discountAmount = CurrencyFormatter.calculateDiscount(subtotal, detail.getDiscountPercentage());
                double totalAmount = subtotal - discountAmount;
                tvTotalPrice.setText(CurrencyFormatter.formatVND(totalAmount));
            }
        }
    }
}
