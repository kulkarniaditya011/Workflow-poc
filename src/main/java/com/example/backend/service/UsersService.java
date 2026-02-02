package com.example.backend.service;

import com.example.backend.dto.CreateSuperAdminDTO;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.dto.UserDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface UsersService {
     ApiResponse<String> signup(@Valid SignUpRequest signUpRequest);

    ApiResponse<String> createSuperAdmin(@Valid CreateSuperAdminDTO request, String tenantId);

    ApiResponse<String> removeUser(String userId);

    ApiResponse<List<UserDTO>> getUsersDepartments(String departmentId);
}
