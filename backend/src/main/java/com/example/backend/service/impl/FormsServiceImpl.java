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
import com.example.backend.model.FormMetadata;
import com.example.backend.model.Forms;
import com.example.backend.repository.FormRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import com.example.backend.service.RestheartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormsServiceImpl implements FormsService {
    private final RestheartService restHeartService;
    private final PagebleObject pagebleObject;
    private final ValidationUtil validationUtil;
    private final Validator validator;
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

    @Override
    public ApiResponse<String> updateForm(String payload, String formId) {
        JsonNode jsonNode = pagebleObject.getJsonNode(payload);
        // Fetch existing
        Map<String, Object> filter = Map.of("formId", formId);
        Forms existing = restHeartService.getWithFilter("forms", filter)
                .map(map -> pagebleObject.convertValue(map, Forms.class))
                .blockFirst();

        if (existing == null) {
            throw new RestApiException("Form not found with formId: " + formId, HttpStatus.NOT_FOUND);
        }

        // Field update map
        Map<String, Consumer<Object>> updaters = getConsumerMap(existing);

        applyPatch(jsonNode, updaters);

        restHeartService.upsert("forms", existing.getId(), existing).block();

        return ResponseUtil.getResponseMessage("Form updated successfully");
    }

    private Map<String, Consumer<Object>> getConsumerMap(Forms existing) {
        Map<String, Consumer<Object>> updaters = new HashMap<>();

        updaters.put("name", v -> existing.setName((String) v));
        updaters.put("description", v -> existing.setDescription((String) v));
        updaters.put("status", v -> existing.setStatus((String) v));
        updaters.put("tenantId", v -> existing.setTenantId((String) v));
        updaters.put("formId", v -> existing.setFormId((String) v));

        updaters.put("metadata", v ->
                existing.setMetadata(pagebleObject.convertValue(v, FormMetadata.class))
        );

        updaters.put("fields", v -> {
            List<FormFieldsDTO> fieldDTOs =
                    pagebleObject.convertValue(v, new TypeReference<List<FormFieldsDTO>>() {});
            List<FormField> validated =
                    fieldDTOs.stream().map(this::validateFromFields)
                            .map(formFieldDTO-> pagebleObject.convertValue(formFieldDTO, FormField.class))
                            .toList();
            existing.setFields(validated);
        });
        return updaters;
    }


    private void applyPatch(JsonNode jsonNode,
                            Map<String, Consumer<Object>> fieldUpdaters) {

        fieldUpdaters.forEach((field, updater) -> {
            if (jsonNode.has(field)) {
                JsonNode node = jsonNode.get(field);
                if (!node.isNull()) {
                    Object value = pagebleObject.convertValue(node, Object.class);
                    updater.accept(value);
                }
            }
        });
    }

    private FormFieldsDTO validateFromFields(FormFieldsDTO fieldsDTO) {
        Set<ConstraintViolation<FormFieldsDTO>> violations = validator.validate(fieldsDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return fieldsDTO;
    }


    @Override
    public ApiResponse<String> deleteForms(String formId) {
        Map<String, Object> filter = Map.of("formId", formId);
        Forms existing = restHeartService.getWithFilter("forms", filter)
                .map(map -> pagebleObject.convertValue(map, Forms.class))
                .blockFirst();

        if (existing == null) {
            throw new RestApiException("Form not found with formId: " + formId, HttpStatus.NOT_FOUND);
        }

        restHeartService.delete("forms", existing.getId()).block();

        return ResponseUtil.getResponseMessage("Form deleted successfully");
    }

}