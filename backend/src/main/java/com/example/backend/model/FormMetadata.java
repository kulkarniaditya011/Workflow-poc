package com.example.backend.model;

import com.example.backend.common.InstantFromMongoDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = InstantFromMongoDeserializer.class)
    private Instant createdAt;

    @JsonDeserialize(using = InstantFromMongoDeserializer.class)
    private Instant updatedAt;
    private String createdBy;
    private Integer version;
}
