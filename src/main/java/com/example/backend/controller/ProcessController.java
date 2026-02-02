package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.RequestProcessDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
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
    public ResponseEntity<ApiResponse<ProcessDTO>> getProcess(@PathVariable("id") String processId) {
        return ResponseEntity.status(HttpStatus.OK).body(processService.getProcessById(processId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Process using process id")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_PROCESS')")
    public ResponseEntity<ApiResponse<String>> updateProcess(@Valid @RequestBody RequestProcessDTO processDTO,
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
    @Operation(summary = "Get Process by workflow id")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<List<ProcessDTO>>> getAllProcesses() {
        return ResponseEntity.status(HttpStatus.OK).body(processService.getAllProcesses());
    }

    @GetMapping("departments/{id}")
    @Operation(summary = "Get process by department")
    @PreAuthorize("hasAuthority('READ_PROCESS')")
    public ResponseEntity<ApiResponse<List<ProcessDTO>>> getWorkflowsByDepartment(@PathVariable("id") String departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(processService.getWorkflowByDepartment(departmentId));
    }


//    @PostMapping("/executions/{id}")
//    @Operation(summary = "Execute a Process by process id")
//    public ResponseEntity<ApiResponse<String>> executeProcess(@PathVariable("id") String processId){
//        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
//    }

//    @GetMapping("next/{id}")
//    @Operation(summary = "Get Next Process by current process id")
//    @PreAuthorize("hasAuthority('READ_PROCESS')")
//    public ResponseEntity<ApiResponse<String>> getNextProcess(@PathVariable("id") String processId){
//        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
//    }

//    @GetMapping("/status/{id}" )
//    @Operation(summary = "Get Process Status by process id")
//    @PreAuthorize("hasAuthority('READ_PROCESS')")
//    public ResponseEntity<ApiResponse<String>> getProcessStatus(@PathVariable("id") String processId) {
//        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
//    }

}

