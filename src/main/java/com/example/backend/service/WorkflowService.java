package com.example.backend.service;

import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.UpdateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface WorkflowService {
    ApiResponse<String> createWorkflow(@Valid CreateWorkflowDTO createWorkflowDTO);

    ApiResponse<WorkflowDTO> getWorkflowById(String workflowId);

    ApiResponse<Page<ProcessDTO>> getProcessForWorkflow(String workflowId, int page, int size, String sortBy, String direction);

    ApiResponse<String> deleteWorkflow(String workflowId);

    ApiResponse<Page<WorkflowDTO>> getAllWorkflows(int page, int size, String sortBy, String direction);

    ApiResponse<Page<WorkflowDTO>> getWorkflowByDepartment(String departmentId, int page, int size, String sortBy, String direction);


    ApiResponse<String> updateWorkflow(String workflowId, @Valid UpdateWorkflowDTO workflowDTO);

    ApiResponse<String> approveWorkflow(String workflowId, String comment);

    ApiResponse<String> rejectWorkflow(String workflowId, String comment);
}

