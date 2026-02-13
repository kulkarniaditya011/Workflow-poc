package com.example.backend.dto;

import com.example.backend.enums.ResourceStatus;
import com.example.backend.model.WorkflowMetadata;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkflowDTO {

    @NotEmpty(message = "Workflow id is required")
    private String workflowId;
    private String name;
    private String description;
    private String departmentId;
    private ResourceStatus status;
    private String version;

    private List<String> processes;

    private WorkflowMetadata metadata;
}
