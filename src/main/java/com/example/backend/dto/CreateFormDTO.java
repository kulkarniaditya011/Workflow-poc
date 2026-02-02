package com.example.backend.dto;

import com.example.backend.model.FormMetadata;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateFormDTO {

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
