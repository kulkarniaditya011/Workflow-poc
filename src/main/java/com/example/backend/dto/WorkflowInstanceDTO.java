package com.example.backend.dto;

import com.example.backend.model.InstanceMetadata;
import com.example.backend.model.Property;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowInstanceDTO {

    private String id;

    @NotNull(message = "Instance id cannot be blank")
    private String instanceId;

    @NotNull(message = "Instance id cannot be blank")
    private String workflowId;

    @NotNull(message = "Tenant id is required for this operation")
    private String tenantId;

    private Map<String, Property> properties;

    private String status;
    private String currentStepId;

    private InstanceMetadata metadata;

}
