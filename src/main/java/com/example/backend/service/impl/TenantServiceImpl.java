package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.RequestTenantDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Tenant;
import com.example.backend.repository.TenantRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    private final ValidationUtil validationUtil;
    private final TenantRepository tenantRepository;
    private final PagebleObject pagebleObject;

    @Override
    public ApiResponse<String> createTenant(RequestTenantDTO requestTenantDTO) {
        validationUtil.validate(requestTenantDTO);
        Tenant tenant = pagebleObject.map(requestTenantDTO, Tenant.class);
        tenant.setId(null);
        tenantRepository.save(tenant);
        return ResponseUtil.getResponseMessage("Tenant created");
    }

    @Override
    public ApiResponse<List<RequestTenantDTO>> getAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll();
        if(tenants.isEmpty()) {
            throw new RestApiException("Tenants not found", HttpStatus.NOT_FOUND);
        }
        List<RequestTenantDTO> requestTenantDTOS = pagebleObject.mapList(tenants, RequestTenantDTO.class);
        return ResponseUtil.getResponse(requestTenantDTOS, "List of Tenants");
    }


    @Override
    public ApiResponse<String> removeTenant(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RestApiException("Tenant not found", HttpStatus.NOT_FOUND));
        tenantRepository.delete(tenant);
        return ResponseUtil.getResponseMessage("Tenant deleted permanently");
    }



    @Override
    public ApiResponse<String> updateTenant(String tenantId, RequestTenantDTO requestTenantDTO) {
        validationUtil.validate(requestTenantDTO);

        Tenant existingTenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RestApiException("Tenant not found", HttpStatus.NOT_FOUND));

        existingTenant.setName(requestTenantDTO.getName());
        existingTenant.setDomain(requestTenantDTO.getDomain());
        existingTenant.setStatus(requestTenantDTO.getStatus());
        existingTenant.setConfig(requestTenantDTO.getConfig());
        existingTenant.setContactInfo(requestTenantDTO.getContactInfo());
        existingTenant.setMetadata(requestTenantDTO.getMetadata());

        tenantRepository.save(existingTenant);

        return ResponseUtil.getResponseMessage("Tenant updated successfully");
    }
}
