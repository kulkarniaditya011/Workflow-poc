package com.example.backend.service;

import com.example.backend.dto.ProcessDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

public interface ProcessService {
    ApiResponse<String> createProcess(@Valid ProcessDTO processDTO);

    ApiResponse<ProcessDTO> updateProcess(String processDTO, String processId);

    ApiResponse<ProcessDTO> getProcessByWorkflow(String workflowId);

    ApiResponse<String> deleteProcess(String processId);
}
