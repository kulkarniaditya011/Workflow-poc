package com.example.backend.dto;

import com.example.backend.model.WorkflowMetadata;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowDTO {

    private String id;

    @NotBlank(message = "Tenant id cannot be blank")
    private String tenantId;

    @NotBlank(message = "Workflow id cannot be blank")
    private String workflowId;

    private String name;

    private String description;

    private List<String> processes;

    private WorkflowMetadata  workflowMetadata;

    private String status= "active";

}
