package com.example.assignmentpod.ui.tab.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentpod.R;
import com.example.assignmentpod.model.chat.Message;
import com.example.assignmentpod.ui.adapter.ChatMessageAdapter;
import com.example.assignmentpod.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private RecyclerView rvMessages;
    private EditText etMessage;
    private MaterialButton btnSend;
    private TextView tvEmptyState;
    private TextView tvOnlineStatus;
    private ProgressBar progressBar;

    private ChatMessageAdapter adapter;
    private String accountId;
    private String chatRoomId;
    private DatabaseReference chatRoomRef;
    private DatabaseReference messagesRef;
    private ValueEventListener messagesListener;

    public ChatFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChatFragment created");
        
        // Get account ID from TokenManager
        accountId = TokenManager.getAccountId();
        if (accountId == null) {
            Log.e(TAG, "Account ID is null!");
            return;
        }
        
        // Build chat room ID
        chatRoomId = "admin_" + accountId;
        Log.d(TAG, "Chat Room ID: " + chatRoomId);
        
        // Initialize Firebase database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        chatRoomRef = firebaseDatabase.getReference("chats").child(chatRoomId);
        messagesRef = chatRoomRef.child("messages");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "ChatFragment onCreateView");
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ChatFragment onViewCreated");
        
        initViews(view);
        initAdapter();
        setupListeners();
        loadMessages();
    }

    private void initViews(View view) {
        rvMessages = view.findViewById(R.id.rv_messages);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvOnlineStatus = view.findViewById(R.id.tv_online_status);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Show latest messages at bottom
        rvMessages.setLayoutManager(layoutManager);
    }

    private void initAdapter() {
        adapter = new ChatMessageAdapter(accountId);
        rvMessages.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        
        // Send on IME action
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void loadMessages() {
        progressBar.setVisibility(View.VISIBLE);
        messagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                
                List<Message> messages = new ArrayList<>();
                
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing message", e);
                    }
                }
                
                // Sort messages by timestamp (oldest first)
                messages.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                
                adapter.setMessages(messages);
                
                // Show/hide empty state
                if (messages.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvMessages.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvMessages.setVisibility(View.VISIBLE);
                    rvMessages.scrollToPosition(messages.size() - 1); // Scroll to latest message
                }
                
                Log.d(TAG, "Loaded " + messages.size() + " messages");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load messages: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        };
        
        messagesRef.addValueEventListener(messagesListener);
        
        // Add child event listener for new messages
        messagesRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        adapter.addMessage(message);
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                        Log.d(TAG, "New message received from: " + message.getSender());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing new message", e);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Child event listener cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        
        if (messageText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create message object
        Message message = new Message();
        message.setSender(accountId);
        message.setText(messageText);
        message.setTimestamp(System.currentTimeMillis());
        
        // Push to Firebase
        messagesRef.push().setValue(message, (error, ref) -> {
            if (error != null) {
                Log.e(TAG, "Failed to send message: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Message sent successfully");
                
                // Update chat room metadata
                chatRoomRef.child("lastMessage").setValue(messageText);
                chatRoomRef.child("lastTimestamp").setValue(System.currentTimeMillis());
                
                // Clear input
                etMessage.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Remove Firebase listeners
        if (messagesListener != null) {
            messagesRef.removeEventListener(messagesListener);
        }
    }
}
