package com.example.backend.dto;

import com.example.backend.enums.ProcessExecutionPattern;
import com.example.backend.model.Assignment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestProcessDTO {

    @NotEmpty(message = "process id is required")
    private String processId;
    @NotEmpty(message = "Process name is required")
    private String name;
    private String description;
    private String departmentId;
    private ProcessExecutionPattern executionPattern;
    private Assignment defaultAssignment;
    @Valid
    @NotEmpty(message = "A process must have at least one step")
    private List<StepsDTO> steps;
}
