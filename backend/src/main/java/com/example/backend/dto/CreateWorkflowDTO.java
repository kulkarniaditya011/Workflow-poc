package com.example.backend.dto;

import com.example.backend.model.WorkflowMetadata;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowDTO {


    @NotBlank(message = "Workflow id cannot be blank")
    private String workflowId;

    private String name;

    private String description;

    private List<String> processId;
    private WorkflowMetadata workflowMetadata;
    private String status= "active";

}
