package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormFieldsDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.FormField;
import com.example.backend.model.FormMetadata;
import com.example.backend.model.Forms;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.FormRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.SecurityUtils;
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

    private static final String FORMS_COLLECTION = "forms";
    private static final String FORM_ID_FIELD = "formId";
    private static final String FORM_NOT_FOUND_MESSAGE = "Form not found with formId: %s";

    private final RestheartService restHeartService;
    private final PagebleObject pagebleObject;
    private final FormRepository formRepository;
    private final ValidationUtil validationUtil;
    private final DepartmentsRepository departmentsRepository;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
        validationUtil.validate(formsDTO);

        String tenantId = SecurityUtils.getTenantId();

        List<FormField> validatedFields = validateAndMapFormFields(formsDTO.getFields());

        Forms form = buildFormFromDTO(formsDTO, tenantId, validatedFields);

        log.info("Creating form for tenant: {} with formId: {}", tenantId, formsDTO.getFormId());

        restHeartService
                .create(FORMS_COLLECTION, form, Forms.class)
                .block();

        return ResponseUtil.getResponseMessage("Form created successfully");
    }

    @Override
    public ApiResponse<FormsDTO> getFormsByFormId(String formId) {
        String tenantId = SecurityUtils.getTenantId();

        FormsDTO form = fetchFormDTO(formId, tenantId);

        log.info("Form retrieved for tenant: {} with formId: {}", tenantId, formId);

        return ResponseUtil.getResponse(form, "Form retrieved successfully");
    }

    @Override
    public ApiResponse<String> updateForm(String payload, String formId) {
        String tenantId = SecurityUtils.getTenantId();
        JsonNode updatePayload = pagebleObject.getJsonNode(payload);

        Forms existingForm = findFormByIdOrThrow(formId, tenantId);

        Map<String, Consumer<Object>> fieldUpdaters = buildFieldUpdateStrategies(existingForm);
        applyPatchToForm(updatePayload, fieldUpdaters);

        restHeartService
                .upsert(FORMS_COLLECTION, existingForm.getId(), existingForm, Forms.class)
                .block();

        return ResponseUtil.getResponseMessage("Form updated successfully");
    }

    @Override
    public ApiResponse<String> deleteForms(String formId) {
        String tenantId = SecurityUtils.getTenantId();

        Forms existingForm = findFormByIdOrThrow(formId, tenantId);

        restHeartService
                .delete(FORMS_COLLECTION, existingForm.getId())
                .block();

        return ResponseUtil.getResponseMessage("Form deleted successfully");
    }

    @Override
    public ApiResponse<List<FormsDTO>> getAllForms() {
        String tenantId = SecurityUtils.getTenantId();

        Map<String, Object> filter = createFormTenantFilter(tenantId);

        List<FormsDTO> forms = restHeartService
                .getWithFilter(FORMS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Forms.class))
                .map(entity -> pagebleObject.map(entity, FormsDTO.class))
                .collectList()
                .block();
        assert forms != null;
        forms.forEach(v-> System.out.println(v.getMetadata().getCreatedAt()));
        return ResponseUtil.getResponse(forms, "Forms retrieved successfully");
    }

    @Override
    public ApiResponse<List<FormsDTO>> getFormsByDepartment(String departmentId) {
        String tenantId= SecurityUtils.getTenantId();
        if(departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty()){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        List<Forms> forms= formRepository.findByTenantIdAndDepartmentId(tenantId,departmentId);
        if (forms.isEmpty()) {
            throw new RestApiException("Forms not found", HttpStatus.NOT_FOUND);
        }
        List<FormsDTO> formsDTOS= pagebleObject.mapList(forms, FormsDTO.class);
        return ResponseUtil.getResponse(formsDTOS, "Forms retrieved successfully");

    }


    /**
     * Validates and maps form field DTOs to FormField entities.
     */
    private List<FormField> validateAndMapFormFields(List<FormFieldsDTO> fieldDTOs) {
        return pagebleObject.mapList(
                fieldDTOs.stream()
                        .map(this::validateFormField)
                        .collect(Collectors.toList()),
                FormField.class
        );
    }

    /**
     * Builds a Forms entity from the CreateFormDTO and validated fields.
     */
    private Forms buildFormFromDTO(CreateFormDTO dto, String tenantId, List<FormField> validatedFields) {
        return Forms.builder()
                .tenantId(tenantId)
                .formId(dto.getFormId())
                .name(dto.getName())
                .description(dto.getDescription())
                .departmentId(dto.getDepartmentId())
                .fields(validatedFields)
                .status(dto.getStatus())
                .metadata(dto.getMetadata())
                .build();
    }

    /**
     * Fetches a form by ID and converts it to FormsDTO.
     * Ensures the form belongs to the current tenant.
     *
     * @throws RestApiException if form is not found
     */
    private FormsDTO fetchFormDTO(String formId, String tenantId) {
        Map<String, Object> filter = createFormFilter(formId, tenantId);

        FormsDTO form = restHeartService
                .getWithFilter(FORMS_COLLECTION, filter)
                .map(map -> objectMapper.convertValue(map, Forms.class))
                .map(entity -> pagebleObject.map(entity, FormsDTO.class))
                .blockFirst();

        if (form == null) {
            throw new RestApiException(
                    String.format(FORM_NOT_FOUND_MESSAGE, formId),
                    HttpStatus.NOT_FOUND
            );
        }

        return form;
    }

    /**
     * Finds a form by ID or throws an exception if not found.
     * Ensures the form belongs to the current tenant.
     *
     * @throws RestApiException if form is not found
     */
    private Forms findFormByIdOrThrow(String formId, String tenantId) {
        Map<String, Object> filter = createFormFilter(formId, tenantId);

        Forms form = restHeartService
                .getWithFilter(FORMS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Forms.class))
                .blockFirst();

        if (form == null) {
            throw new RestApiException(
                    String.format(FORM_NOT_FOUND_MESSAGE, formId),
                    HttpStatus.NOT_FOUND
            );
        }

        return form;
    }

    /**
     * Creates a filter map for querying by form ID and tenant ID.
     */
    private Map<String, Object> createFormFilter(String formId, String tenantId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put(FORM_ID_FIELD, formId);
        return filter;
    }

    /**
     * Creates a filter map for querying by tenant ID only (for fetching all forms of a tenant).
     */
    private Map<String, Object> createFormTenantFilter(String tenantId) {
        return Map.of("tenantId", tenantId);
    }

    /**
     * Builds a map of field update strategies for patching a form.
     */
    private Map<String, Consumer<Object>> buildFieldUpdateStrategies(Forms existingForm) {
        Map<String, Consumer<Object>> strategies = new HashMap<>();

        strategies.put("name", value ->
                existingForm.setName((String) value));

        strategies.put("description", value ->
                existingForm.setDescription((String) value));

        strategies.put("status", value ->
                existingForm.setStatus((String) value));

        strategies.put("tenantId", value ->
                existingForm.setTenantId((String) value));

        strategies.put("formId", value ->
                existingForm.setFormId((String) value));

        strategies.put("metadata", value ->
                existingForm.setMetadata(
                        pagebleObject.convertValue(value, FormMetadata.class)
                )
        );

        strategies.put("fields", value -> {
            List<FormFieldsDTO> fieldDTOs = pagebleObject.convertValue(
                    value,
                    new TypeReference<>() {
                    }
            );

            List<FormField> validatedFields = fieldDTOs.stream()
                    .map(this::validateFormField)
                    .map(dto -> pagebleObject.convertValue(dto, FormField.class))
                    .toList();

            existingForm.setFields(validatedFields);
        });

        return strategies;
    }

    /**
     * Applies JSON patch updates to a form using the provided update strategies.
     */
    private void applyPatchToForm(JsonNode patchPayload,
                                  Map<String, Consumer<Object>> fieldUpdaters) {
        fieldUpdaters.forEach((fieldName, updateStrategy) -> {
            if (patchPayload.has(fieldName)) {
                JsonNode fieldNode = patchPayload.get(fieldName);

                if (!fieldNode.isNull()) {
                    Object fieldValue = pagebleObject.convertValue(fieldNode, Object.class);
                    updateStrategy.accept(fieldValue);
                }
            }
        });
    }

    /**
     * Validates a form field DTO and returns it if valid.
     *
     * @throws ConstraintViolationException if validation fails
     */
    private FormFieldsDTO validateFormField(FormFieldsDTO fieldDTO) {
        Set<ConstraintViolation<FormFieldsDTO>> violations = validator.validate(fieldDTO);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return fieldDTO;
    }
}