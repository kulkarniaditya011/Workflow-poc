package com.example.backend.controller;

import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/create-workflow")
    public ResponseEntity<ApiResponse<CreateWorkflowDTO>> createWorkflow(@Valid @RequestBody CreateWorkflowDTO createWorkflowDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(workflowService.createWorkflow(createWorkflowDTO));
    }


}
