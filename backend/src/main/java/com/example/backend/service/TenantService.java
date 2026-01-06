package com.example.backend.service;

import com.example.backend.dto.TenantDTO;
import com.example.backend.response.ApiResponse;

import java.util.List;

public interface TenantService {
    ApiResponse<String> createTenant(TenantDTO tenantDTO);

    ApiResponse<List<TenantDTO>> getAllTenants();
}
