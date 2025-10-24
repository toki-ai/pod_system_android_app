package com.example.assignmentpod.model.chat;

/**
 * ChatRoom model for Firebase chat metadata
 */
public class ChatRoom {
    private String lastMessage;     // Last message text
    private long lastTimestamp;     // Timestamp of last message

    public ChatRoom() {
        // Default constructor required for Firebase
    }

    public ChatRoom(String lastMessage, long lastTimestamp) {
        this.lastMessage = lastMessage;
        this.lastTimestamp = lastTimestamp;
    }

    // Getters
    public String getLastMessage() {
        return lastMessage;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    // Setters
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "lastMessage='" + lastMessage + '\'' +
                ", lastTimestamp=" + lastTimestamp +
                '}';
    }
}
