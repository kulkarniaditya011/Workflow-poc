package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.TenantDTO;
import com.example.backend.model.Tenant;
import com.example.backend.repository.TenantRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final PagebleObject pagebleObject;

    @Override
    public ApiResponse<String> createTenant(TenantDTO tenantDTO) {

        Tenant tenant = pagebleObject.map(tenantDTO, Tenant.class);
        tenant.setId(null);
        tenantRepository.save(tenant);
        return ResponseUtil.getResponseMessage("Tenant created");
    }

    @Override
    public ApiResponse<List<TenantDTO>> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        List<TenantDTO> tenantDTOS = pagebleObject.mapList(tenants, TenantDTO.class);
        return ResponseUtil.getResponse(tenantDTOS, "List of Tenants");
    }
}
