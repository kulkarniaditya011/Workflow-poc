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


//    @Override
//    public ApiResponse<FormsDTO> getFormById(String formId) {
//        Form form = restHeartService.getById(FORMS_COLLECTION, formId)
//                .map(map -> pagebleObject.map(map, Form.class))
//                .block();
//
//        if (form == null) {
//            throw new RuntimeException("Form not found with id: " + formId);
//        }
//
//        FormsDTO formDTO = pagebleObject.map(form, FormsDTO.class);
//        return ResponseUtil.getResponse(formDTO, "Form retrieved successfully");
//    }
//
//    @Override
//    public ApiResponse<List<FormsDTO>> getAllForms() {
//        List<FormsDTO> forms = restHeartService.getAll(FORMS_COLLECTION)
//                .map(map -> pagebleObject.map(map, Form.class))
//                .map(form -> pagebleObject.map(form, FormsDTO.class))
//                .collectList()
//                .block();
//
//        return ResponseUtil.getResponse(forms, "Forms retrieved successfully");
//    }
//
//    @Override
//    public ApiResponse<FormsDTO> updateForm(String formId, FormsDTO formsDTO) {
//        validationUtil.validate(formsDTO);
//
//        // Validate and map form fields
//        List<FormField> formFields = pagebleObject.mapList(
//                formsDTO.getFields()
//                        .stream()
//                        .map(this::validateFromFields)
//                        .collect(Collectors.toList()),
//                FormField.class);
//
//        // Map DTO to Form entity
//        Form form = pagebleObject.map(formsDTO, Form.class);
//        form.setId(formId);
//        form.setFields(formFields);
//
//        // Update in RestHeart
//        Map updatedMap = restHeartService.update(FORMS_COLLECTION, formId,
//                        pagebleObject.map(form, Map.class))
//                .block();
//
//        Form updatedForm = pagebleObject.map(updatedMap, Form.class);
//        FormsDTO updatedDTO = pagebleObject.map(updatedForm, FormsDTO.class);
//
//        return ResponseUtil.getResponse(updatedDTO, "Form updated successfully");
//    }
//
//    @Override
//    public ApiResponse<Void> deleteForm(String formId) {
//        restHeartService.delete(FORMS_COLLECTION, formId).block();
//        return ResponseUtil.getResponse(null, "Form deleted successfully");
//    }

    private FormFieldsDTO validateFromFields(FormFieldsDTO fieldsDTO) {
        Set<ConstraintViolation<FormFieldsDTO>> violations = validator.validate(fieldsDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return fieldsDTO;
    }

}