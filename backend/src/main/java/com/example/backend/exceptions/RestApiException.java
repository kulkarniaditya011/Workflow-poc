package com.example.backend.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RestApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final List<String> errorMessage=new ArrayList<>();


    public RestApiException(Object message, HttpStatus httpStatus) {
        super(message.toString());
        this.httpStatus = httpStatus;
        this.errorMessage.add(message.toString());
    }

    public RestApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorMessage.add(message);
    }
    public RestApiException(List<String> message, HttpStatus httpStatus) {
        super(message.toString());
        this.httpStatus = httpStatus;
        this.errorMessage.addAll(message);
    }

}
