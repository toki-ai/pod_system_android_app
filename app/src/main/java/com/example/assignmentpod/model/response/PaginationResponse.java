package com.example.assignmentpod.model.response;

import com.google.gson.annotations.SerializedName;

public class PaginationResponse<T> {
    @SerializedName("code")
    private int code = 200;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPage")
    private int totalPage;

    @SerializedName("recordPerPage")
    private int recordPerPage;

    @SerializedName("totalRecord")
    private int totalRecord;

    public PaginationResponse() {}

    public PaginationResponse(int code, String message, T data, int currentPage, int totalPage, int recordPerPage, int totalRecord) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.recordPerPage = recordPerPage;
        this.totalRecord = totalRecord;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getRecordPerPage() {
        return recordPerPage;
    }

    public void setRecordPerPage(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }
}
