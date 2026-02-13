package com.example.backend.dto;

import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.FormMetadata;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FormResponseDTO {
    private String tenantId;
    private String formId;
    private String name;
    private String description;
    private List<String> departmentId;
    private List<FormFieldsDTO> fields;
    private String status;
    private ApprovalMetadata approval;
    private FormMetadata metadata = new FormMetadata();
}
