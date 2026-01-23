package com.example.backend.dto;

import com.example.backend.model.FormField;
import com.example.backend.model.FormMetadata;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

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

    @Valid
    private List<FormFieldsDTO> fields;

    private String status;

    private FormMetadata metadata = new FormMetadata();
}
