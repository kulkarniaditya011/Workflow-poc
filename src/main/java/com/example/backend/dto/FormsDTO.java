package com.example.backend.dto;

import com.example.backend.model.FormMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FormsDTO {

    private String id;

    private String tenantId;

    @NotEmpty(message = "form-id cannot be null")
    private String formId;

    private String name;

    private String description;
    private String departmentId;

    @Valid
    private List<FormFieldsDTO> fields;

    private String status;

    private FormMetadata metadata = new FormMetadata();
}
