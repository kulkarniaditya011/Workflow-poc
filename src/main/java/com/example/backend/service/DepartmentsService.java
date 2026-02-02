package com.example.backend.service;

import com.example.backend.dto.DepartmentsDTO;
import com.example.backend.dto.RequestDepartmentsDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface DepartmentsService {
    ApiResponse<String> createDepartment(@Valid RequestDepartmentsDTO request);

    ApiResponse<List<DepartmentsDTO>> getAllDepartments();

    ApiResponse<String> assignManager(String departmentId, String managerId);

}
