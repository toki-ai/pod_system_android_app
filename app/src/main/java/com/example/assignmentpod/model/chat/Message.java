package com.example.assignmentpod.model.chat;

/**
 * Message model for Firebase chat
 */
public class Message {
    private String sender;      // User ID of the sender (e.g., "admin" or "user123")
    private String text;        // Message text content
    private long timestamp;     // Message timestamp in milliseconds

    public Message() {
        // Default constructor required for Firebase
    }

    public Message(String sender, String text, long timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
