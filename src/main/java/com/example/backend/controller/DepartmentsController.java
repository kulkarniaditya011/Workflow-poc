package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.DepartmentsDTO;
import com.example.backend.dto.RequestDepartmentsDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.DepartmentsService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "departments", description = "Department APIs")
public class DepartmentsController {
    private final DepartmentsService departmentsService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_DEPARTMENT')")
    @Operation(summary = "Creates a Department")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> createDepartment(@Valid @RequestBody RequestDepartmentsDTO request){

        return ResponseEntity.status(HttpStatus.OK).body(departmentsService.createDepartment(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_DEPARTMENT')")
    @Operation(summary = "Gets all Departments present in a Tenant")
    public ResponseEntity<ApiResponse<List<DepartmentsDTO>>> getAllDepartments(){
       return ResponseEntity.status(HttpStatus.OK).body(departmentsService.getAllDepartments());
    }

    @PatchMapping("/{id}")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_DEPARTMENT')")
    @Operation(summary = "Assigns a manger to department")
    public ResponseEntity<ApiResponse<String>> assignManagerToDepartment(@PathVariable(value = "id") String departmentId, @RequestParam String managerId){
        return ResponseEntity.status(HttpStatus.OK).body(departmentsService.assignManager(departmentId, managerId));
    }

}
