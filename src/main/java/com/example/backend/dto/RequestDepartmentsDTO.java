package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDepartmentsDTO {
    @NotEmpty(message = "Department ID cannot be empty")
    private String departmentId;

    private String name;
    private String description;

    private String managerId;
}
