package com.example.backend.dto;

import com.example.backend.model.WorkflowMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkflowDTO {
    private String tenantId;
    private String workflowId;
    private String name;
    private String description;
    private String departmentId;
    private String status;
    private String version;

    private List<String> processId;

    private WorkflowMetadata metadata;
}
