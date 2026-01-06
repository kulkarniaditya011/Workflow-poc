package com.example.backend.model;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metadata {
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy; // User who created the entity
    private String currentOwner; // Current owner (can change during workflow)
    private String workflowInstanceId;
}
