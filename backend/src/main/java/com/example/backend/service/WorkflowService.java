package com.example.backend.service;

import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

public interface WorkflowService {
    ApiResponse<CreateWorkflowDTO> createWorkflow(@Valid CreateWorkflowDTO createWorkflowDTO);
}
