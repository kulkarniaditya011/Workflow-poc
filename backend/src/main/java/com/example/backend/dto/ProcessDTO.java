package com.example.backend.dto;

import com.example.backend.model.Steps;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessDTO {

    @NotEmpty(message = "process id is required")
    private String processId;

    @NotEmpty(message = "Workflow id cannot be empty")
    private String WorkflowId;

    @NotEmpty(message = "A process should have a name")
    private String processName;
    private Integer sequence;
    private String processType;
    private String executionPattern; //sequential or parallel
    private List<String> assignedRoles;
    private List<String> assignedUsers;
    @Valid
    private List<StepsDTO> processSteps;
}
