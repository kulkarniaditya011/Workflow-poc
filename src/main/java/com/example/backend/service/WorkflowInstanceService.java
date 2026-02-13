package com.example.backend.service;


import com.example.backend.dto.RequestWorkflowInstanceDTO;
import com.example.backend.dto.ResponseWorkflowInstanceDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import org.springframework.data.domain.Page;

public interface WorkflowInstanceService {
    ApiResponse<String> startWorkflow(String id, RequestWorkflowInstanceDTO instance);

    ApiResponse<String> completeSteps(String id, String stepKey);

    ApiResponse<String> stopWorkflow(String workflowInstanceId);

    ApiResponse<Page<WorkflowDTO>> getAllRunningWorkflows(int page, int size, String sortBy, String direction);
}
