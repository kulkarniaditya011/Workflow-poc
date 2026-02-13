package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.UpdateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowService;
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
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflows", description = "Workflow APIs")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @AdminApi
    @PreAuthorize("hasAuthority('CREATE_WORKFLOW')")
    public ResponseEntity<ApiResponse<String>> createWorkflow(@Valid @RequestBody CreateWorkflowDTO createWorkflowDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(createWorkflowDTO));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<Page<WorkflowDTO>>> getWorkflows(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(defaultValue = "name") String sortBy,
                                                                       @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getAllWorkflows(page, size, sortBy, direction));
    }

    @PutMapping("/{id}")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_WORKFLOW')")
    public ResponseEntity<ApiResponse<String>> updateWorkflow(@Valid @RequestBody UpdateWorkflowDTO workflowDTO,
                                                              @PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.updateWorkflow(workflowId,workflowDTO));
    }

    @DeleteMapping("/{id}")
    @AdminApi
    @PreAuthorize("hasAuthority('DELETE_WORKFLOW')")
    public ResponseEntity<ApiResponse<String>> deleteWorkflow(@PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.deleteWorkflow(workflowId));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get workflow by workflow id")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<WorkflowDTO>> getWorkflowById(@PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getWorkflowById(workflowId));
    }

    @GetMapping("/process/{id}")
    @Operation(summary = "Get Process for a workflow by workflow id")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<Page<ProcessDTO>>> getProcessForWorkflowByWorkflowId(@PathVariable("id") String workflowId,
                                                                                           @RequestParam(defaultValue = "0") int page,
                                                                                           @RequestParam(defaultValue = "10") int size,
                                                                                           @RequestParam(defaultValue = "name") String sortBy,
                                                                                           @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getProcessForWorkflow(workflowId, page, size, sortBy, direction));
    }

    @GetMapping("departments/{id}")
    @Operation(summary = "Get workflows by department")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<Page<WorkflowDTO>>> getWorkflowsByDepartment(@PathVariable("id") String departmentId,
                                                                                   @RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "name") String sortBy,
                                                                                   @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getWorkflowByDepartment(departmentId, page, size, sortBy, direction));
    }

    @PatchMapping("approve/{id}")
    @Operation(summary = "Approve workflow")
    @PreAuthorize("hasAuthority('APPROVE_WORKFLOW')")
    public ResponseEntity<ApiResponse<String>> approveWorkflow(@PathVariable("id") String workflowId, @RequestBody String comment){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.approveWorkflow(workflowId, comment));
    }

    @PatchMapping("reject/{id}")
    @Operation(summary = "Reject workflow")
    @PreAuthorize("hasAuthority('REJECT_WORKFLOW')")
    public ResponseEntity<ApiResponse<String>> rejectWorkflow(@PathVariable("id") String workflowId, @RequestBody String comment){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.rejectWorkflow(workflowId, comment));
    }
}
