package com.example.backend.service;

import com.example.backend.dto.CreateSuperAdminDTO;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.dto.UserDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UsersService {
     ApiResponse<String> signup(@Valid SignUpRequest signUpRequest);

    ApiResponse<String> createSuperAdmin(@Valid CreateSuperAdminDTO request, String tenantId);

    ApiResponse<String> removeUser(String userId);

    ApiResponse<Page<UserDTO>> getUsersDepartments(String departmentId, int page, int size, String sortBy, String direction);
}
