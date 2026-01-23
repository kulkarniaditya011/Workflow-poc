package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Workflow;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RestheartService;
import com.example.backend.service.WorkflowService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service implementation for managing workflow operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private static final String WORKFLOWS_COLLECTION = "workflows";
    private static final String WORKFLOW_ID_FIELD = "workflowId";
    private static final String WORKFLOW_ALREADY_EXISTS_MESSAGE = "Workflow with id: %s already exists";
    private static final String WORKFLOW_CREATED_MESSAGE = "Workflow created successfully with id: %s";

    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;

    /**
     * Creates a new workflow.
     * Validates the input and ensures no workflow with the same ID already exists.
     *
     * @param createWorkflowDTO the workflow creation data
     * @return ApiResponse with success message
     * @throws RestApiException if a workflow with the same ID already exists
     */
    @Override
    public ApiResponse<String> createWorkflow(CreateWorkflowDTO createWorkflowDTO) {
        validationUtil.validate(createWorkflowDTO);

        String tenantId = SecurityUtils.getTenantId();

        ensureWorkflowDoesNotExist(createWorkflowDTO.getWorkflowId(), tenantId);

        Workflow workflow = buildWorkflowFromDTO(createWorkflowDTO, tenantId);

        log.info("Creating workflow for tenant: {} with workflowId: {}", tenantId, createWorkflowDTO.getWorkflowId());

        saveWorkflow(workflow);

        return ResponseUtil.getResponseMessage(
                String.format(WORKFLOW_CREATED_MESSAGE, createWorkflowDTO.getWorkflowId())
        );
    }

    // ==================== Private Helper Methods ====================

    /**
     * Ensures a workflow with the given ID does not already exist for the current tenant.
     *
     * @param workflowId the workflow ID to check
     * @param tenantId the tenant ID
     * @throws RestApiException if workflow already exists
     */
    private void ensureWorkflowDoesNotExist(String workflowId, String tenantId) {
        Map<String, Object> filter = createWorkflowFilter(workflowId, tenantId);

        Workflow existingWorkflow = restheartService
                .getWithFilter(WORKFLOWS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .blockFirst();

        if (existingWorkflow != null) {
            throw new RestApiException(
                    String.format(WORKFLOW_ALREADY_EXISTS_MESSAGE, workflowId),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Creates a filter map for querying by workflow ID and tenant ID.
     */
    private Map<String, Object> createWorkflowFilter(String workflowId, String tenantId) {
        Map<String, Object> filter = new java.util.HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put(WORKFLOW_ID_FIELD, workflowId);
        return filter;
    }

    /**
     * Builds a Workflow entity from the CreateWorkflowDTO.
     */
    private Workflow buildWorkflowFromDTO(CreateWorkflowDTO dto, String tenantId) {
        return Workflow.builder()
                .tenantId(tenantId)
                .workflowId(dto.getWorkflowId())
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .processId(dto.getProcessId())
                .metadata(dto.getWorkflowMetadata())
                .build();
    }

    /**
     * Saves the workflow to the database.
     */
    private void saveWorkflow(Workflow workflow) {
        restheartService
                .create(WORKFLOWS_COLLECTION, workflow, Workflow.class)
                .block();
    }
}