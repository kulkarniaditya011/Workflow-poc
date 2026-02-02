package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.RequestTenantDTO;
import com.example.backend.dto.ResponseTenantDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant APIs")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    @AdminApi
    @PreAuthorize("hasAuthority('CREATE_TENANT')")
    public ResponseEntity<ApiResponse<String>> createTenant(@Valid @RequestBody RequestTenantDTO requestTenantDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(requestTenantDTO));
    }

    @GetMapping
    @Operation(summary = "Get all tenants")
    @AdminApi
    @PreAuthorize("hasAuthority('READ_TENANT')")
    public ResponseEntity<ApiResponse<List<RequestTenantDTO>>> getTenants() {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.getAllTenants());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing tenant")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_TENANT')")
    public ResponseEntity<ApiResponse<String>> updateTenant(@Valid @RequestBody RequestTenantDTO requestTenantDTO,
                                                            @PathVariable("id") String tenantId) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.updateTenant(tenantId,requestTenantDTO));
    }

    @DeleteMapping("{id}")
    @AdminApi
    @PreAuthorize("hasAuthority('DELETE_TENANT')")
    public ResponseEntity<ApiResponse<String>> deleteTenant(@PathVariable("id") String tenantId) {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.removeTenant(tenantId));
    }

}
