package com.example.backend.dto;

import com.example.backend.enums.ResourceStatus;
import com.example.backend.model.FormField;
import com.example.backend.model.FormMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UpdateFormDTO {
    private String name;
    private String description;
    private List<String> departmentId;
    private List<FormField> fields;
    private ResourceStatus status;
    private FormMetadata metadata;
}
