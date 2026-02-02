package com.example.backend.dto;

import com.example.backend.enums.ProcessExecutionPattern;
import com.example.backend.enums.ProcessStatus;
import com.example.backend.model.Assignment;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessDTO {
    private String processId;
    private String tenantId;
    private String name;
    private String description;
    private ProcessStatus status;
    private String departmentId;
    private Boolean latest;
    private ProcessExecutionPattern executionPattern;
    private Assignment assignment;
    private List<StepsDTO> steps;
}
