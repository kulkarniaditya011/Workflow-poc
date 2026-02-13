package com.example.backend.service;

import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.RequestProcessDTO;
import com.example.backend.dto.ResponseStepDTO;
import com.example.backend.dto.UpdateProcessDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProcessService {
    ApiResponse<String> createProcess(@Valid RequestProcessDTO processDTO);

    ApiResponse<String> deleteProcess(String processId);

    ApiResponse<ProcessDTO> getProcessById(String processId);

    ApiResponse<String> updateProcess(UpdateProcessDTO dto, String processId);

    ApiResponse<Page<ProcessDTO>> getAllProcesses(int page, int size, String sortBy, String direction);

    ApiResponse<Page<ProcessDTO>> getProcessByDepartment(String departmentId, int page, int size, String sortBy, String direction);

    ApiResponse<String> approveProcess(String processId, String comment);

    ApiResponse<String> rejectProcess(String processId, String reason);


    ApiResponse<Page<ResponseStepDTO>> getStepsByProcess(String processId, int page, int size, String sortBy, String direction);
}
