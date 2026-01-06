package com.example.backend.common;

import com.example.backend.response.ApiResponse;

import java.util.Date;

public class ResponseUtil {

    public static <T> ApiResponse<T> getResponse(T data, Object message) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .timestamp(new Date())
                .build();
    }

    public static <T> ApiResponse<T> getResponseMessage(Object message){
        return ApiResponse.<T>builder()
                .message(message)
                .timestamp(new Date())
                .build();
    }

}
