package com.example.backend.dto;

import com.example.backend.model.FormMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

    private List<FormFieldsDTO> fields;

    private String status;

    private FormMetadata metadata = new FormMetadata();
}
