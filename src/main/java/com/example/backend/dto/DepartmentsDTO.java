package com.example.backend.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DepartmentsDTO {
    private String tenantId;
    private String departmentId;
    private String name;
    private String description;
    private String managerId;
}
