package com.example.backend.dto;

import com.example.backend.enums.ResourceStatus;
import com.example.backend.model.InstanceMetadata;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResponseWorkflowInstanceDTO {
    private String id;
    private String instanceId;
    private String workflowId;
    private String tenantId;
    private List<String> processId;
    private ResourceStatus status;
    private String currentStepId;
    private InstanceMetadata metadata= new InstanceMetadata();
}
