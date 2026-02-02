package com.example.backend.service;

import com.example.backend.dto.RequestTenantDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface TenantService {
    ApiResponse<String> createTenant(RequestTenantDTO requestTenantDTO);

    ApiResponse<List<RequestTenantDTO>> getAllTenants();

    ApiResponse<String> removeTenant(String tenantId);

    ApiResponse<String> updateTenant(String tenantId, @Valid RequestTenantDTO requestTenantDTO);
}
