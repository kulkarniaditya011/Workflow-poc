package com.example.backend.service;

import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormResponseDTO;
import com.example.backend.dto.UpdateFormDTO;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface FormsService {
    ApiResponse<String> createForms(@Valid CreateFormDTO formsDTO);

    ApiResponse<FormResponseDTO> getFormsByFormId(String formId);

    ApiResponse<String> updateForm(UpdateFormDTO payload, String formId);

    ApiResponse<String> deleteForms(String formId);

    ApiResponse<Page<FormResponseDTO>> getAllForms(int page, int size, String sortBy, String direction);

    ApiResponse<Page<FormResponseDTO>> getFormsByDepartment(String id, int page, int size, String sortBy, String direction);

    ApiResponse<String> approveForm(String formId, String comment);

    ApiResponse<String> rejectForm(String formId, String reason);
}

