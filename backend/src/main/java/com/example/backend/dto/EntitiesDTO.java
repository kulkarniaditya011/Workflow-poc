package com.example.backend.dto;

import com.example.backend.model.Metadata;
import com.example.backend.model.Property;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntitiesDTO {

    private String id;

    @NotNull(message = "Tenant id is required")
    private String tenantId;

    private String entityId;

    @NotEmpty(message = "Name cannot be empty")
    private String entityName;

    private Map<String, Property> properties;

    private String status;

    private Metadata metadata;
}
