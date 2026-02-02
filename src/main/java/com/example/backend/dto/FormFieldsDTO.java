package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldsDTO {

    @NotEmpty(message = "Field id cannot be empty")
    private String fieldId;
    private String label;

    @NotEmpty(message = "Type is required")
    private String type; // text, number, date, dropdown, etc.
    private boolean required;
    private List<String> options; // for dropdown
    private String validation;
}
