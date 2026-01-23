package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.StepsDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Process;
import com.example.backend.model.Steps;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.SecurityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Service implementation for managing process operations including creation, retrieval, updates, and deletion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private static final String PROCESS_COLLECTION = "process";
    private static final String PROCESS_ID_FIELD = "processId";
    private static final String WORKFLOW_ID_FIELD = "workflowId";
    private static final String PROCESS_NOT_FOUND_MESSAGE = "Process not found with %s: %s";
    private static final String PROCESS_ALREADY_EXISTS_MESSAGE = "Process with id %s already exists";
    private static final String PROCESS_CREATED_MESSAGE = "Process with id %s has been created";
    private static final String PROCESS_DELETED_MESSAGE = "Process with id %s has been deleted";

    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;

    @Override
    public ApiResponse<String> createProcess(ProcessDTO processDTO) {
        validationUtil.validate(processDTO);

        String tenantId = SecurityUtils.getTenantId();

        List<Steps> validatedSteps = validateAndMapSteps(processDTO.getProcessSteps());
        log.info("Steps after validation and mapping: {}", validatedSteps);

        ensureProcessDoesNotExist(processDTO.getProcessId(), tenantId);

        Process process = buildProcessFromDTO(processDTO, tenantId, validatedSteps);

        ProcessDTO savedProcess = saveProcess(process);

        log.info("Process created for tenant: {} with processId: {}", tenantId, savedProcess.getProcessId());

        return ResponseUtil.getResponseMessage(
                String.format(PROCESS_CREATED_MESSAGE, savedProcess.getProcessId())
        );
    }

    @Override
    public ApiResponse<ProcessDTO> updateProcess(String processPayload, String processId) {
        String tenantId = SecurityUtils.getTenantId();
        JsonNode updatePayload = pagebleObject.getJsonNode(processPayload);
        ProcessDTO processDTO = pagebleObject.readValue(processPayload, ProcessDTO.class);

        Process existingProcess = findProcessByIdOrThrow(processId, tenantId);

        Map<String, Consumer<Object>> fieldUpdaters = buildFieldUpdateStrategies(existingProcess);
        applyPatchToProcess(updatePayload, fieldUpdaters);

        restheartService
                .upsert(PROCESS_COLLECTION, existingProcess.getId(), existingProcess, Process.class)
                .block();

        return ResponseUtil.getResponseMessage("Process updated");

    }
    @Override
    public ApiResponse<ProcessDTO> getProcessByWorkflow(String workflowId) {
        String tenantId = SecurityUtils.getTenantId();

        log.info("Retrieving process with workflow id: {} for tenant: {}", workflowId, tenantId);

        ProcessDTO process = fetchProcessDTOByWorkflowId(workflowId, tenantId);

        log.info("Process retrieved: {}", process);

        return ResponseUtil.getResponse(process, "process retrieved successfully");
    }

    @Override
    public ApiResponse<String> deleteProcess(String processId) {
        String tenantId = SecurityUtils.getTenantId();

        Process existingProcess = findProcessByIdOrThrow(processId, tenantId);

        restheartService
                .delete(PROCESS_COLLECTION, existingProcess.getId())
                .block();

        return ResponseUtil.getResponseMessage(
                String.format(PROCESS_DELETED_MESSAGE, processId)
        );
    }

    // ==================== Private Helper Methods ====================

    /**
     * Validates and maps step DTOs to Steps entities.
     */
    private List<Steps> validateAndMapSteps(List<StepsDTO> stepDTOs) {
        return pagebleObject.mapList(
                stepDTOs.stream()
                        .map(validationUtil::validateFields)
                        .toList(),
                Steps.class
        );
    }

    /**
     * Ensures a process with the given ID does not already exist for the current tenant.
     *
     * @throws RestApiException if process already exists
     */
    private void ensureProcessDoesNotExist(String processId, String tenantId) {
        Map<String, Object> filter = createProcessFilter(processId, tenantId);

        Process existingProcess = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(obj -> pagebleObject.convertValue(obj, Process.class))
                .blockFirst();

        if (existingProcess != null) {
            throw new RestApiException(
                    String.format(PROCESS_ALREADY_EXISTS_MESSAGE, processId),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Builds a Process entity from the ProcessDTO and validated steps.
     */
    private Process buildProcessFromDTO(ProcessDTO dto, String tenantId, List<Steps> validatedSteps) {
        return Process.builder()
                .tenantId(tenantId)
                .processId(dto.getProcessId())
                .workflowId(dto.getWorkflowId())
                .processName(dto.getProcessName())
                .sequence(dto.getSequence())
                .processSteps(validatedSteps)
                .processType(dto.getProcessType())
                .executionPattern(dto.getExecutionPattern())
                .assignedRoles(dto.getAssignedRoles())
                .assignedUsers(dto.getAssignedUsers())
                .build();
    }

    /**
     * Saves a process and returns the saved ProcessDTO.
     */
    private ProcessDTO saveProcess(Process process) {
        return pagebleObject.map(
                restheartService
                        .create(PROCESS_COLLECTION, process, Process.class)
                        .block(),
                ProcessDTO.class
        );
    }

    /**
     * Finds a process by ID or throws an exception if not found.
     * Ensures the process belongs to the current tenant.
     *
     * @throws RestApiException if process is not found
     */
    private Process findProcessByIdOrThrow(String processId, String tenantId) {
        Map<String, Object> filter = createProcessFilter(processId, tenantId);

        Process process = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Process.class))
                .blockFirst();

        if (process == null) {
            throw new RestApiException(
                    String.format(PROCESS_NOT_FOUND_MESSAGE, PROCESS_ID_FIELD, processId),
                    HttpStatus.NOT_FOUND
            );
        }

        return process;
    }

    /**
     * Fetches a process by workflow ID and converts it to ProcessDTO.
     * Ensures the process belongs to the current tenant.
     *
     * @throws RestApiException if process is not found
     */
    private ProcessDTO fetchProcessDTOByWorkflowId(String workflowId, String tenantId) {
        Map<String, Object> filter = createProcessWorkflowFilter(workflowId, tenantId);

        ProcessDTO process = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Process.class))
                .map(entity -> pagebleObject.map(entity, ProcessDTO.class))
                .blockFirst();

        if (process == null) {
            throw new RestApiException(
                    String.format(PROCESS_NOT_FOUND_MESSAGE, WORKFLOW_ID_FIELD, workflowId),
                    HttpStatus.NOT_FOUND
            );
        }

        return process;
    }

    /**
     * Creates a filter map for querying by process ID and tenant ID.
     */
    private Map<String, Object> createProcessFilter(String processId, String tenantId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put(PROCESS_ID_FIELD, processId);
        return filter;
    }

    /**
     * Creates a filter map for querying by workflow ID and tenant ID.
     */
    private Map<String, Object> createProcessWorkflowFilter(String workflowId, String tenantId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put(WORKFLOW_ID_FIELD, workflowId);
        return filter;
    }

    /**
     * Builds a map of field update strategies for patching a process.
     */
    private Map<String, Consumer<Object>> buildFieldUpdateStrategies(Process existingProcess) {
        Map<String, Consumer<Object>> strategies = new HashMap<>();

        strategies.put(PROCESS_ID_FIELD, value ->
                existingProcess.setProcessId((String) value));

        strategies.put("WorkflowId", value ->
                existingProcess.setWorkflowId((String) value));

        strategies.put("processName", value ->
                existingProcess.setProcessName((String) value));

        strategies.put("sequence", value ->
                existingProcess.setSequence((Integer) value));

        strategies.put("processType", value ->
                existingProcess.setProcessType((String) value));

        strategies.put("executionPattern", value ->
                existingProcess.setExecutionPattern((String) value));

        strategies.put("assignedRoles", value ->
                existingProcess.setAssignedRoles(
                        pagebleObject.convertValue(value, new TypeReference<List<String>>() {})
                )
        );

        strategies.put("assignedUsers", value ->
                existingProcess.setAssignedUsers(
                        pagebleObject.convertValue(value, new TypeReference<List<String>>() {})
                )
        );

        strategies.put("processSteps", value -> {
            List<StepsDTO> stepDTOs = pagebleObject.convertValue(
                    value,
                    new TypeReference<>() {
                    }
            );

            List<Steps> validatedSteps = stepDTOs.stream()
                    .map(validationUtil::validateFields)
                    .map(dto -> pagebleObject.convertValue(dto, Steps.class))
                    .toList();

            existingProcess.setProcessSteps(validatedSteps);
        });

        return strategies;
    }

    /**
     * Applies JSON patch updates to a process using the provided update strategies.
     */
    private void applyPatchToProcess(JsonNode patchPayload,
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
}