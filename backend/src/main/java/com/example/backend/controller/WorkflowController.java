package com.example.backend.controller;

import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createWorkflow(@Valid @RequestBody CreateWorkflowDTO createWorkflowDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(createWorkflowDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getWorkflows(){
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.getResponseMessage("test"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateWorkflow(@Valid @RequestBody WorkflowDTO workflowDTO){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteWorkflow(@RequestParam String workflowId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }


}
