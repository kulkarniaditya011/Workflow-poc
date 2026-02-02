package com.example.backend.service;

import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.UpdateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface WorkflowService {
    ApiResponse<String> createWorkflow(@Valid CreateWorkflowDTO createWorkflowDTO);

    ApiResponse<WorkflowDTO> getWorkflowById(String workflowId);

    ApiResponse<List<ProcessDTO>> getProcessForWorkflow(String workflowId);

    ApiResponse<String> deleteWorkflow(String workflowId);

    ApiResponse<List<WorkflowDTO>> getAllWorkflows();

    ApiResponse<List<WorkflowDTO>> getWorkflowByDepartment(String departmentId);


    ApiResponse<String> updateWorkflow(String workflowId, @Valid UpdateWorkflowDTO workflowDTO);
}
