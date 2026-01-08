package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormFieldsDTO;
import com.example.backend.dto.FormResponseDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.FormField;
import com.example.backend.model.Forms;
import com.example.backend.repository.FormRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import com.example.backend.service.RestheartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormsServiceImpl implements FormsService {
    private final RestheartService restHeartService;
    private final PagebleObject pagebleObject;
    private final ValidationUtil validationUtil;
    private final Validator validator;
    private final FormRepository formRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
        validationUtil.validate(formsDTO);

        // Validate and map form fields
        List<FormField> formFields = pagebleObject.mapList(
                formsDTO.getFields()
                        .stream()
                        .map(this::validateFromFields)
                        .collect(Collectors.toList()),
                FormField.class
        );

        Forms forms = Forms.builder()
                .tenantId(formsDTO.getTenantId())
                .formId(formsDTO.getFormId())
                .name(formsDTO.getName())
                .description(formsDTO.getDescription())
                .fields(formFields)
                .status(formsDTO.getStatus())
                .metadata(formsDTO.getMetadata())
                .build();

        log.info("Form object:{}", forms.toString());
        FormResponseDTO FormDTO= pagebleObject.map(restHeartService
                .create("forms", forms, Forms.class)
                .block(), FormResponseDTO.class);
        return ResponseUtil.getResponseMessage(String.format("Form created successfully with id: %s", FormDTO.getFormId()));
    }

    @Override
    public ApiResponse<FormsDTO> getFormsByFormId(String formId) {
        Map<String, Object> filter = Map.of("formId", formId);
        log.info("Form id:{}", formId);
        FormsDTO forms=restHeartService.getWithFilter("forms",filter)
                        .map(map-> objectMapper.convertValue(map, Forms.class))
                        .map(form-> pagebleObject.map(form, FormsDTO.class))
                        .blockFirst();
        log.info("Form :{}", forms);
        if (forms==null) {
            throw new RestApiException(String.format("Form not found with formId: " + formId),HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(forms, "Form retrieved successfully");
    }

    private FormFieldsDTO validateFromFields(FormFieldsDTO fieldsDTO) {
        Set<ConstraintViolation<FormFieldsDTO>> violations = validator.validate(fieldsDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return fieldsDTO;
    }

}