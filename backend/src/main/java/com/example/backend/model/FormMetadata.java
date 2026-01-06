package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormMetadata {
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private Integer version;
}
