package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflows", description = "Workflow APIs")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @AdminApi
    public ResponseEntity<ApiResponse<String>> createWorkflow(@Valid @RequestBody CreateWorkflowDTO createWorkflowDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(createWorkflowDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getWorkflows(){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @PutMapping("/{id}")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> updateWorkflow(@Valid @RequestBody WorkflowDTO workflowDTO,
                                                              @PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping("/{id}")
    @AdminApi
    public ResponseEntity<ApiResponse<String>> deleteWorkflow(@PathVariable("id") String workflowId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }

    @PostMapping("/executions/{id}" )
    public ResponseEntity<ApiResponse<String>> executeWorkflow(@PathVariable("id") String workflowId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

}
