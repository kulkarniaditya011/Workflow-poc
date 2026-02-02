package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.common.ResponseUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<List<WorkflowDTO>>> getWorkflows(){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getAllWorkflows());
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

//    @PostMapping("/executions/{id}" )
//    public ResponseEntity<ApiResponse<String>> executeWorkflow(@PathVariable("id") String workflowId) {
//        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
//    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow by workflow id")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<WorkflowDTO>> getWorkflowById(@PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getWorkflowById(workflowId));
    }

    @GetMapping("/process/{id}")
    @Operation(summary = "Get Process for a workflow by workflow id")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<List<ProcessDTO>>> getProcessForWorkflowByWorkflowId(@PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getProcessForWorkflow(workflowId));
    }

    @GetMapping("departments/{id}")
    @Operation(summary = "Get workflows by department")
    @PreAuthorize("hasAuthority('READ_WORKFLOW')")
    public ResponseEntity<ApiResponse<List<WorkflowDTO>>> getWorkflowsByDepartment(@PathVariable("id") String departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.getWorkflowByDepartment(departmentId));
    }

}
