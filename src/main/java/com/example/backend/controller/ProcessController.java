package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.RequestProcessDTO;
import com.example.backend.dto.ResponseStepDTO;
import com.example.backend.dto.UpdateProcessDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
@Tag(name = "Process", description = "Workflow process APIs")
public class ProcessController {
    private final ProcessService processService;

    @PostMapping
    @Operation(summary = "Create a Process")
    @AdminApi
    @PreAuthorize("hasAuthority('CREATE_PROCESS')")
    public ResponseEntity<ApiResponse<String>> createProcess(@Valid @RequestBody RequestProcessDTO processDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.createProcess(processDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "get process by process id")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<ProcessDTO>> getProcessByProcessId(@PathVariable("id") String processId) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.getProcessById(processId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Process using process id")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_PROCESS')")
    public ResponseEntity<ApiResponse<String>> updateProcess(@Valid @RequestBody UpdateProcessDTO processDTO,
                                                             @PathVariable("id") String processId) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.updateProcess(processDTO, processId));
    }

    @DeleteMapping("/{id}")
    @AdminApi
    @Operation(summary = "Delete a Process by process id")
    @PreAuthorize("hasAuthority('DELETE_PROCESS')")
    public ResponseEntity<ApiResponse<String>> deleteProcess(@PathVariable("id") String processId) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.deleteProcess(processId));
    }

    @GetMapping
    @Operation(summary = "Get all processes")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<Page<ProcessDTO>>> getAllProcesses(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "name") String sortBy,
                                                                         @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.getAllProcesses(page, size, sortBy, direction));
    }

    @GetMapping("departments/{id}")
    @Operation(summary = "Get process by department")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<Page<ProcessDTO>>> getWorkflowsByDepartment(@PathVariable("id") String departmentId,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(defaultValue = "name") String sortBy,
                                                                                  @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(processService.getProcessByDepartment(departmentId, page, size, sortBy, direction));
    }

    @PatchMapping("/approvals/{id}/approve")
    @Operation(summary = "Approve a process")
    @PreAuthorize("hasAuthority('APPROVE_PROCESS')")
    public ResponseEntity<ApiResponse<String>> approveProcess(@PathVariable("id") String processId, @RequestBody String comment) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.approveProcess(processId, comment));
    }

    @PatchMapping("/approvals/{id}/reject")
    @Operation(summary = "Reject a process")
    @PreAuthorize("hasAuthority('REJECT_PROCESS')")
    public ResponseEntity<ApiResponse<String>> rejectProcess(@PathVariable("id") String processId, @RequestBody String reason) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.rejectProcess(processId, reason));
    }

    @GetMapping("steps/{id}")
    @Operation(summary = "Get steps of a process")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<Page<ResponseStepDTO>>> getStepsOfProcess(@PathVariable("id") String processId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "name") String sortBy,
                                                                                @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(processService.getStepsByProcess(processId, page, size, sortBy, direction));
    }

}

