package com.example.backend.controller;

import com.example.backend.dto.TenantDTO;
import com.example.backend.repository.TenantRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/create-tenant")
    public ResponseEntity<ApiResponse<String>> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.createTenant(tenantDTO));
    }

    @GetMapping("/get-tenants")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getTenants() {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.getAllTenants());
    }


}
