package com.example.backend.service;

import com.example.backend.dto.RoleDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

public interface RoleService {
    ApiResponse<String> createRole(@Valid RoleDTO role);
}
