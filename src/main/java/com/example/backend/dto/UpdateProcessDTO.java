package com.example.backend.dto;

import com.example.backend.enums.ProcessExecutionPattern;
import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.Assignment;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateProcessDTO {

    private String processId;
    private String name;
    private String description;
    private String departmentId;
    private ProcessExecutionPattern executionPattern;
    private Assignment assignment;
    private ApprovalMetadata approval;
    private List<StepsDTO> steps;
}
