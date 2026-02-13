package com.example.backend.service;

import com.example.backend.dto.RequestTenantDTO;
import com.example.backend.dto.ResponseTenantDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TenantService {
    ApiResponse<String> createTenant(RequestTenantDTO requestTenantDTO);

    ApiResponse<Page<ResponseTenantDTO>> getAllTenants(int page, int size, String sortBy, String direction);

    ApiResponse<String> removeTenant(String tenantId);

    ApiResponse<String> updateTenant(String tenantId, @Valid RequestTenantDTO requestTenantDTO);
}
