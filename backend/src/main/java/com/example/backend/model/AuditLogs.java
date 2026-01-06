package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "auditLogs")
public class AuditLogs {
    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String action;

    private String userId;
    private String details;
    private String ipAddress;
    private Map<String, Object> metadata;

    @Indexed
    @Builder.Default
    private Instant timestamp = Instant.now();
}
