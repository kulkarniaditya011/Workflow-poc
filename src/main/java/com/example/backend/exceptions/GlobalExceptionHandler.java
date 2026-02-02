package com.example.backend.exceptions;

import com.example.backend.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Object>> handeRestApiException(RestApiException exception, WebRequest request) {
        return new ResponseEntity<>(ApiResponse.builder().message(exception.getMessage())
                .errors(request.getDescription(false))
                .timestamp(new Date())
                .build(), exception.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex,
                                                                                      WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            errors.add(cv.getMessage());
        }
        return new ResponseEntity<>(ApiResponse.builder().timestamp(new Date()).message(errors)
                .errors(request.getDescription(false)).build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                                     WebRequest request) {
        List<String> errors = new ArrayList<>();
        for(FieldError error: exception.getBindingResult().getFieldErrors()){
            errors.add(error.getDefaultMessage());
        }
        Collections.sort(errors);
        return new ResponseEntity<>(ApiResponse.builder().message(errors)
                .errors(request.getDescription(false))
                .timestamp(new Date())
                .build(), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, WebRequest request) {
        Throwable rootCause = exception.getMostSpecificCause();

        if (rootCause instanceof MismatchedInputException mismatched) {
            return new ResponseEntity<>(
                    ApiResponse.builder()
                            .message("Invalid JSON structure or data type")
                            .errors(mismatched.getPathReference())
                            .timestamp(new Date())
                            .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
                ApiResponse.builder()
                        .message("Malformed JSON request")
                        .errors(exception.getMessage())
                        .timestamp(new Date())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest request) {

        return new ResponseEntity<>(
                ApiResponse.builder()
                        .message(exception.getMessage())
                        .errors("Invalid request argument")
                        .timestamp(new Date())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }



}
