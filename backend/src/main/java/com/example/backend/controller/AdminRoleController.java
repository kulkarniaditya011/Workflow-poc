package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.RoleDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles")
@RestController
public class AdminRoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a role")
    @AdminApi
    @PreAuthorize("hasRole('SUPER_ADMIN') and hasAuthority('CREATE_ROLE')")
    public ResponseEntity<ApiResponse<String>> createRole(@Valid @RequestBody RoleDTO role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(role));
    }

}
