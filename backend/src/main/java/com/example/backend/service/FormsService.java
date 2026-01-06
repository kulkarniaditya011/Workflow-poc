package com.example.backend.service;

import com.example.backend.dto.FormsDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

public interface FormsService {
    ApiResponse<FormsDTO> createForms(@Valid FormsDTO formsDTO);
}
