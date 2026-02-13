package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.*;
import com.example.backend.enums.ResourceStatus;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.FormField;
import com.example.backend.model.Forms;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.FormRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.SecurityUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormsServiceImpl implements FormsService {

    private final RestheartService restHeartService;
    private final PagebleObject pagebleObject;
    private final FormRepository formRepository;
    private final ValidationUtil validationUtil;
    private final DepartmentsRepository departmentsRepository;
    private final Validator validator;

    @Override
    public ApiResponse<String> createForms(CreateFormDTO formsDTO) {
        validationUtil.validate(formsDTO);

        String tenantId = SecurityUtils.getTenantId();

        List<FormField> validatedFields = validateAndMapFormFields(formsDTO.getFields());

        Forms form = buildFormFromDTO(formsDTO, tenantId, validatedFields);

        log.info("Creating form for tenant: {} with formId: {}", tenantId, formsDTO.getFormId());

        restHeartService
                .create("forms", form, Forms.class)
                .block();

        return ResponseUtil.getResponseMessage("Form created successfully");
    }

    @Override
    public ApiResponse<FormResponseDTO> getFormsByFormId(String formId) {
        String tenantId = SecurityUtils.getTenantId();

        Forms form = findForm(formId, tenantId);
        FormResponseDTO response= pagebleObject.map(form, FormResponseDTO.class);

        log.info("Form retrieved for tenant: {} with formId: {}", tenantId, formId);

        return ResponseUtil.getResponse(response, "Form retrieved successfully");
    }

    @Override
    public ApiResponse<String> updateForm(UpdateFormDTO payload, String formId) {
        String tenantId = SecurityUtils.getTenantId();
        Forms existing = findForm(formId, tenantId);
        validationUtil.validate(payload);
        Forms updated = pagebleObject.convertValue(payload, Forms.class);

        // preserve immutable fields
        updated.setId(existing.getId());
        updated.setTenantId(existing.getTenantId());
        updated.setFormId(existing.getFormId());

        restHeartService
                .upsert("forms", existing.getId(), updated, Forms.class)
                .block();

        return ResponseUtil.getResponseMessage("Form updated successfully");
    }

    @Override
    public ApiResponse<String> deleteForms(String formId) {
        String tenantId = SecurityUtils.getTenantId();

        Forms existingForm = findForm(formId, tenantId);

        restHeartService
                .delete("forms", existingForm.getId())
                .block();

        return ResponseUtil.getResponseMessage("Form deleted successfully");
    }

    @Override
    public ApiResponse<Page<FormResponseDTO>> getAllForms(int page, int size, String sortBy, String direction) {
        Sort sort= direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String tenantId = SecurityUtils.getTenantId();
        Page<FormResponseDTO> forms = formRepository.findByTenantId(tenantId, pageable)
                .map(form-> pagebleObject.map(form,FormResponseDTO.class));
        log.info(forms.toString());
        if (forms.isEmpty()) {
            throw new RestApiException("Forms not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(forms, "Forms retrieved successfully");
    }

    @Override
    public ApiResponse<Page<FormResponseDTO>> getFormsByDepartment(String departmentId, int page, int size, String sortBy, String direction) {
        String tenantId= SecurityUtils.getTenantId();
        Sort sort= direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if(doesDepartmentExists(tenantId, departmentId)){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        Page<FormResponseDTO> forms= formRepository.findByTenantIdAndDepartmentId(tenantId,departmentId,pageable)
                .map(form->{log.info(form.toString());
                return pagebleObject.map(form,FormResponseDTO.class);}
                );

        if (forms.isEmpty()) {
            throw new RestApiException("Forms not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(forms, "Forms retrieved successfully");

    }

    @Override
    public ApiResponse<String> approveForm(String formId, String comment) {
        String tenantId= SecurityUtils.getTenantId();
        Forms forms= formRepository.findByTenantIdAndFormId(tenantId, formId)
                .orElseThrow(() -> new RestApiException("Form not found", HttpStatus.NOT_FOUND));
        ApprovalMetadata approval= forms.getApproval();
        if (approval == null) {
            throw new RestApiException(
                    "Approval metadata not found for form",
                    HttpStatus.BAD_REQUEST
            );
        }

        if(approval.getStatus().equals(ResourceStatus.APPROVED)){
            return ResponseUtil.getResponseMessage("Form already approved");
        }
        if (!approval.getStatus().equals(ResourceStatus.PENDING)) {
            return ResponseUtil.getResponseMessage("Form maybe rejected");
        }
        approval.setStatus(ResourceStatus.APPROVED);
        approval.setActionBy(SecurityUtils.getUsername());
        approval.setActionAt(Instant.now());
        approval.setComment(comment);
        formRepository.save(forms);
        return ResponseUtil.getResponseMessage("Form approved successfully");
    }

    @Override
    public ApiResponse<String> rejectForm(String formId, String reason) {
        String tenantId = SecurityUtils.getTenantId();
        String approver= SecurityUtils.getUsername();
        Forms form= formRepository.findByTenantIdAndFormId(tenantId, formId)
                .orElseThrow(() -> new RestApiException("Form not found", HttpStatus.NOT_FOUND));

        ApprovalMetadata approval= form.getApproval();

        validateRejectState(approval);

        if (approval.getStatus() == ResourceStatus.REJECTED) {
            return ResponseUtil.getResponseMessage("Form already rejected");
        }

        approval.setStatus(ResourceStatus.REJECTED);
        approval.setActionBy(approver);
        approval.setActionAt(Instant.now());
        approval.setComment(reason);

        formRepository.save(form);
        return ResponseUtil.getResponseMessage("Form rejected successfully");
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
                .approval(ApprovalMetadata.builder().status(ResourceStatus.PENDING).build())
                .metadata(dto.getMetadata())
                .build();
    }



    /**
     * Finds a form by ID or throws an exception if not found.
     * Ensures the form belongs to the current tenant.
     *
     * @throws RestApiException if form is not found
     */
    private Forms findForm(String formId, String tenantId) {
        return formRepository.findByTenantIdAndFormId(tenantId, formId)
                .orElseThrow(()-> new RestApiException("Form not found", HttpStatus.NOT_FOUND));
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

    private boolean doesDepartmentExists(String tenantId, String departmentId) {
        return departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty();
    }

    private void validateRejectState(ApprovalMetadata approval) {

        if (approval == null) {
            throw new RestApiException("Form metadata not found", HttpStatus.BAD_REQUEST);
        }

        if (approval.getStatus() == ResourceStatus.APPROVED) {
            throw new RestApiException("Form process cannot be rejected", HttpStatus.BAD_REQUEST);
        }
    }
}