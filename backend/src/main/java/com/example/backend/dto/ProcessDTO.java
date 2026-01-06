package com.example.backend.dto;

import com.example.backend.model.Steps;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessDTO {
    private String id;

    @NotNull(message = "process id is required")
    private String processId;

    @NotNull(message = "Workflow id cannot be empty")
    private String WorkflowId;

    @NotBlank(message = "A process should have a name")
    private String processName;
    private Integer sequence;
    private String processType;
    private String executionPattern; //sequential or parallel
    private List<String> assignedRoles;
    private List<String> assignedUsers;
    private List<StepsDTO> processSteps;
}
