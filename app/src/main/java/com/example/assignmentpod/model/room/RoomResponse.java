package com.example.assignmentpod.model.room;

import java.util.List;

public class RoomResponse {
    private int code;
    private String message;
    private List<Room> data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<Room> getData() {
        return data;
    }
}
