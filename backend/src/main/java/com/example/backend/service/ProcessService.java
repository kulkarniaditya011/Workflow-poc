package com.example.backend.service;

import com.example.backend.dto.ProcessDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

public interface ProcessService {
    ApiResponse<String> createProcess(@Valid ProcessDTO processDTO);
}
