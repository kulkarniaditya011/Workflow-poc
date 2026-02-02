package com.example.backend.service;

import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.RequestProcessDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface ProcessService {
    ApiResponse<String> createProcess(@Valid RequestProcessDTO processDTO);
    

    ApiResponse<String> deleteProcess(String processId);

    ApiResponse<ProcessDTO> getProcessById(String processId);

    ApiResponse<String> updateProcess(RequestProcessDTO dto, String processId);

    ApiResponse<List<ProcessDTO>> getAllProcesses();

    ApiResponse<List<ProcessDTO>> getWorkflowByDepartment(String departmentId);
}
