package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.TenantDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant APIs")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(tenantDTO));
    }

    @GetMapping
    @Operation(summary = "Get all tenants")
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getTenants() {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.getAllTenants());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tenant")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> updateTenant(@Valid @RequestBody TenantDTO tenantDTO,
                                                            @PathVariable("id") String tenantId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping("{id}")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> deleteTenant(@PathVariable("id") String tenantId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }

}
