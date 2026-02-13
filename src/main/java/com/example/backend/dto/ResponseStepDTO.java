package com.example.backend.dto;

import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.Assignment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStepDTO {
    private String stepKey;
    private String name;
    private Integer order;
    private String type;
    private Assignment assignment;
    private ApprovalMetadata approval;
    private String formId;
}
