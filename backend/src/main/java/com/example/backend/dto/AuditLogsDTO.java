package com.example.backend.dto;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogsDTO {

    private String id;

    private String tenantId;

    private String action;

    private String userId;
    private String details;
    private String ipAddress;
    private Map<String, Object> metadata;

    private Instant timestamp = Instant.now();
}
