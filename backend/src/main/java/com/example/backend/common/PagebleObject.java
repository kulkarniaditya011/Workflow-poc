package com.example.backend.common;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PagebleObject {
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;


    public <S, T> T map(S source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {

        return sourceList.stream()
                .map(s -> modelMapper.map(s, targetClass)).collect(Collectors.toList());

    }

    public <T> T readValue(String content, Class<T> targetClass) {
        try {
            return objectMapper.readValue(content, targetClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public JsonNode getJsonNode(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    public <T> T convertValue(Object from, TypeReference<T> type) {
        return objectMapper.convertValue(from, type);
    }
}
