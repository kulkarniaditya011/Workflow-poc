package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormField {
    private String fieldId;
    private String label;
    private String type; // text, number, date, dropdown, etc.
    private boolean required;
    private List<String> options; // for dropdown
    private String validation;
}
