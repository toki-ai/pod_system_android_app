package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.order.Order;
import com.example.assignmentpod.utils.CurrencyFormatter;
import com.example.assignmentpod.utils.DateTimeFormatterUtil;
import com.example.assignmentpod.utils.OrderStatusMapper;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orders;
    private OrderActionListener actionListener;

    public interface OrderActionListener {
        void onViewInvoice(Order order, int position);
        void onReorder(Order order, int position);
        void onSupport(Order order, int position);
        void onBrowseRooms();
    }

    public OrderAdapter(List<Order> orders, OrderActionListener actionListener) {
        this.orders = orders;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new OrderViewHolder(
                (View) inflater.inflate(R.layout.item_order_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, position, actionListener);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateData(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutOrderSummary;
        private final TextView tvOrderId;
        private final TextView tvOrderStatus;
        private final ImageButton btnExpand;
        private final TextView tvOrderDate;
        private final TextView tvItemsCount;
        private final TextView tvTotalAmount;
        private final FrameLayout layoutExpandedContent;
        private final RecyclerView recyclerViewOrderDetails;
        private final View dividerExpanded;
        private final LinearLayout layoutActionButtons;
        private final Button btnViewInvoice;
        private final Button btnReorder;
        private final Button btnSupport;

        private boolean isExpanded = false;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutOrderSummary = itemView.findViewById(R.id.layout_order_summary);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            btnExpand = itemView.findViewById(R.id.btn_expand);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvItemsCount = itemView.findViewById(R.id.tv_items_count);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            layoutExpandedContent = itemView.findViewById(R.id.layout_expanded_content);
            recyclerViewOrderDetails = itemView.findViewById(R.id.recycler_view_order_details);
            dividerExpanded = itemView.findViewById(R.id.divider_expanded);
            layoutActionButtons = itemView.findViewById(R.id.layout_action_buttons);
            btnViewInvoice = itemView.findViewById(R.id.btn_view_invoice);
            btnReorder = itemView.findViewById(R.id.btn_reorder);
            btnSupport = itemView.findViewById(R.id.btn_support);
        }

        public void bind(Order order, int position, OrderActionListener actionListener) {
            // Order Summary
            tvOrderId.setText(order.getOrderId() != null ? order.getOrderId().substring(0, Math.min(8, order.getOrderId().length())) + "..." : "N/A");
            tvOrderDate.setText(DateTimeFormatterUtil.formatOrderDate(order.getCreatedAt()));
            tvOrderStatus.setText(OrderStatusMapper.getDisplayStatus(order.getOrderStatus()));
            tvOrderStatus.setBackgroundColor(OrderStatusMapper.getStatusColor(order.getOrderStatus()));
            tvTotalAmount.setText(CurrencyFormatter.formatVND(order.getFinalAmount() > 0 ? order.getFinalAmount() : order.getTotalAmount()));

            if (order.getOrderDetails() != null) {
                tvItemsCount.setText(order.getOrderDetails().size() + " Item" + (order.getOrderDetails().size() > 1 ? "s" : ""));
            } else {
                tvItemsCount.setText("0 Items");
            }

            // Expand/Collapse Logic
            btnExpand.setOnClickListener(v -> {
                if (isExpanded) {
                    collapse();
                } else {
                    expand(order);
                }
            });

            layoutOrderSummary.setOnClickListener(v -> {
                if (isExpanded) {
                    collapse();
                } else {
                    expand(order);
                }
            });

            // Action Buttons
            btnViewInvoice.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onViewInvoice(order, position);
                }
            });

            btnReorder.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onReorder(order, position);
                }
            });

            btnSupport.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onSupport(order, position);
                }
            });

            // Collapse by default
            collapse();
        }

        private void expand(Order order) {
            isExpanded = true;
            layoutExpandedContent.setVisibility(View.VISIBLE);
            dividerExpanded.setVisibility(View.VISIBLE);
            layoutActionButtons.setVisibility(View.VISIBLE);

            // Set up order details adapter
            OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(order.getOrderDetails());
            recyclerViewOrderDetails.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recyclerViewOrderDetails.setAdapter(orderDetailAdapter);

            btnExpand.animate().rotation(180).setDuration(300).start();
        }

        private void collapse() {
            isExpanded = false;
            layoutExpandedContent.setVisibility(View.GONE);
            dividerExpanded.setVisibility(View.GONE);
            layoutActionButtons.setVisibility(View.GONE);
            btnExpand.animate().rotation(0).setDuration(300).start();
        }
    }
}
