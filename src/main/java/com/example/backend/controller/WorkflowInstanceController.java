package com.example.backend.controller;

import com.example.backend.dto.RequestWorkflowInstanceDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instances")
@RequiredArgsConstructor
@Tag(name = "Workflow instance", description = "Instance APIs")
public class WorkflowInstanceController {

    private final WorkflowInstanceService workflowInstanceService;

    @PostMapping("start/{id}")
    @Operation(summary = "Starts a workflow")
    @PreAuthorize("hasAuthority('CREATE_INSTANCE')")
    public ResponseEntity<ApiResponse<String>> startWorkflow(@PathVariable("id") String id,
                                                             @RequestBody RequestWorkflowInstanceDTO instance) {
        return ResponseEntity.status(HttpStatus.OK).body(workflowInstanceService.startWorkflow(id, instance));
    }

    @PostMapping("/{id}/steps/{stepKey}/complete")
    @Operation(summary = "completes a step from workflow")
    @PreAuthorize("hasAuthority('UPDATE_INSTANCE')")
    public ResponseEntity<ApiResponse<String>> completeStep(@PathVariable("id") String id,
                                                            @PathVariable String stepKey){
        return ResponseEntity.status(HttpStatus.OK).body(workflowInstanceService.completeSteps(id, stepKey));
    }

    @PostMapping("/stop/{id}")
    @Operation(summary = "stops a workflow")
    @PreAuthorize("hasAuthority('STOP_INSTANCE')")
    public ResponseEntity<ApiResponse<String>> stopWorkflow(@PathVariable("id") String workflowInstanceId){
        return ResponseEntity.status(HttpStatus.OK).body(workflowInstanceService.stopWorkflow(workflowInstanceId));
    }

    @GetMapping("/running")
    @Operation(summary = "Returns all the workflows that are currently running")
    public ResponseEntity<ApiResponse<Page<WorkflowDTO>>> getRunningWorkflows(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(defaultValue = "name") String sortBy,
                                                                              @RequestParam(defaultValue = "asc") String direction){
        return ResponseEntity.status(HttpStatus.OK).body(workflowInstanceService.getAllRunningWorkflows(page,size,sortBy,direction));
    }

}
