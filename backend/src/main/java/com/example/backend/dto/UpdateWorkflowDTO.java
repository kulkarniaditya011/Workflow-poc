package com.example.backend.dto;

import com.example.backend.model.Process;
import com.example.backend.model.WorkflowMetadata;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWorkflowDTO {
    private String id;
    private String workflowId;

    private String name;
    private String description;
    private String status;
    private String version;

    private List<String> processes;

    private WorkflowMetadata metadata;
}
