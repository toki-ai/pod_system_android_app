package com.example.assignmentpod.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.chat.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying chat messages
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
    private static final String TAG = "ChatMessageAdapter";
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<Message> messages = new ArrayList<>();
    private String currentUserId;

    public ChatMessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        if (message != null) {
            this.messages.add(message);
            notifyItemInserted(this.messages.size() - 1);
        }
    }

    public void clearMessages() {
        this.messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSender().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        if (viewType == VIEW_TYPE_SENT) {
            layoutId = R.layout.item_message_sent;
        } else {
            layoutId = R.layout.item_message_received;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageText;
        private TextView tvTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageText = itemView.findViewById(R.id.tv_message);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        public void bind(Message message) {
            tvMessageText.setText(message.getText());
            
            // Format timestamp to readable time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = sdf.format(new Date(message.getTimestamp()));
            tvTimestamp.setText(timeString);
        }
    }
}
