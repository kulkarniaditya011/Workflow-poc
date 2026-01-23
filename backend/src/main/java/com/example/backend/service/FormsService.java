package com.example.backend.service;

import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface FormsService {
    ApiResponse<String> createForms(@Valid CreateFormDTO formsDTO);

    ApiResponse<FormsDTO> getFormsByFormId(String formId);

    ApiResponse<String> updateForm(String payload, String formId);

    ApiResponse<String> deleteForms(String formId);

    ApiResponse<List<FormsDTO>> getAllForms();
}
