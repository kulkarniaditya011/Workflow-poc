package com.example.backend.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;

    public <T> void validate(Object data){
        Set<ConstraintViolation<Object>> violations = validator.validate(data);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }

    public <T> T validateFields(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return obj;
    }
}
