package com.example.backend.controller;

import com.example.backend.common.ResponseUtil;
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
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(tenantDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantDTO>>> getTenants() {
        return ResponseEntity.status(HttpStatus.OK).body(tenantService.getAllTenants());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteTenant(@RequestParam String tenantId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }

}
