package com.example.backend.service;

import com.example.backend.dto.DepartmentsDTO;
import com.example.backend.dto.RequestDepartmentsDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DepartmentsService {
    ApiResponse<String> createDepartment(@Valid RequestDepartmentsDTO request);

    ApiResponse<Page<DepartmentsDTO>> getAllDepartments(int page, int size, String sortBy, String direction);

    ApiResponse<String> assignManager(String departmentId, String managerId);

}
