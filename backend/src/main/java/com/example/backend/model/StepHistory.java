package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepHistory {
    private String stepId;
    private String action;
    private String performedBy;
    private Instant performedAt;
    private Map<String, Object> data;
}
