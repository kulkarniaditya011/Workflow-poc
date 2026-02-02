package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.UpdateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Process;
import com.example.backend.model.Workflow;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.WorkflowRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RestheartService;
import com.example.backend.service.WorkflowService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final DepartmentsRepository departmentsRepository;
    private final WorkflowRepository workflowRepository;
    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;

    /**
     * Creates a new workflow.
     * Validates the input and ensures no workflow with the same ID already exists.
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

    @Override
    public ApiResponse<WorkflowDTO> getWorkflowById(String workflowId) {
        String tenantId = SecurityUtils.getTenantId();

        Map<String, Object> filter = createWorkflowFilter(workflowId, tenantId);

        Workflow workflow = restheartService
                .getWithFilter(WORKFLOWS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .blockFirst();

        if (workflow == null) {
            throw new RestApiException(
                    String.format("Workflow with id: %s not found", workflowId),
                    HttpStatus.NOT_FOUND
            );
        }

        WorkflowDTO workflowDTO = pagebleObject.map(workflow, WorkflowDTO.class);

        return ResponseUtil.getResponse(workflowDTO,"Workflow retrieved successfully");
    }

    @Override
    public ApiResponse<List<ProcessDTO>> getProcessForWorkflow(String workflowId) {
        String tenantId = SecurityUtils.getTenantId();

        Map<String, Object> workflowFilter = createWorkflowFilter(workflowId, tenantId);

        Workflow workflow = restheartService
                .getWithFilter(WORKFLOWS_COLLECTION, workflowFilter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .blockFirst();

        if (workflow == null) {
            throw new RestApiException(
                    String.format("Workflow with id: %s not found", workflowId),
                    HttpStatus.NOT_FOUND
            );
        }
        List<String> processIdList = workflow.getProcessId();
        log.info("process ids {}: ", processIdList);
        if (processIdList == null || processIdList.isEmpty()) {
            return ResponseUtil.getResponse(
                    List.of(),
                    "No processes configured for workflow"
            );
        }

    List<ProcessDTO> processes= processIdList.stream()
            .map(processIds-> {
                Map<String, Object> processFilter = new HashMap<>();
                processFilter.put("tenantId", tenantId);
                processFilter.put("processId", processIds);

                Process process = restheartService
                        .getWithFilter("process", processFilter)
                        .doOnNext(p-> log.info("process {}: ", p))
                        .map(map -> pagebleObject.convertValue(map, Process.class))
                        .blockFirst();

                if (process == null) {
                    throw new RestApiException(
                            String.format("Process with id: %s not found", processIds),
                            HttpStatus.NOT_FOUND
                    );
                }

                return pagebleObject.map(process, ProcessDTO.class);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return  ResponseUtil.getResponse(processes,"Process retrieved successfully");
    }

    @Override
    public ApiResponse<String> deleteWorkflow(String workflowId) {
        String tenantId = SecurityUtils.getTenantId();

        Map<String, Object> filter = createWorkflowFilter(workflowId, tenantId);

        Workflow workflow = restheartService
                .getWithFilter(WORKFLOWS_COLLECTION, filter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .blockFirst();

        if (workflow == null) {
            throw new RestApiException(
                    String.format("Workflow with id: %s not found", workflowId),
                    HttpStatus.NOT_FOUND
            );
        }
        restheartService
                .delete("workflows", workflow.getId())
                .block();
        return ResponseUtil.getResponseMessage(
                String.format("Workflow with id: %s deleted successfully", workflowId)
        );
    }

    @Override
    public ApiResponse<List<WorkflowDTO>> getAllWorkflows() {
        String tenantId= SecurityUtils.getTenantId();
        Map<String, Object> filter = Map.of("tenantId", tenantId);
        List<WorkflowDTO> workflowDTOS= restheartService
                .getWithFilter("workflows", filter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .map(workflow -> pagebleObject.map(workflow, WorkflowDTO.class))
                .collectList()
                .block();
        return ResponseUtil.getResponse(workflowDTOS, "Workflows retrieved successfully");
    }

    @Override
    public ApiResponse<List<WorkflowDTO>> getWorkflowByDepartment(String departmentId) {
        String tenantId= SecurityUtils.getTenantId();
        if(departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty()){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        List<Workflow> workflows= workflowRepository.findByTenantIdAndDepartmentId(tenantId,departmentId);
        if (workflows == null || workflows.isEmpty()) {
            throw new RestApiException("Workflows not found", HttpStatus.NOT_FOUND);
        }
        List<WorkflowDTO> workflowDTOS= pagebleObject.mapList(workflows, WorkflowDTO.class);
        return ResponseUtil.getResponse(workflowDTOS, "Workflow retrieved successfully");

    }

    @Override
    public ApiResponse<String> updateWorkflow(String workflowId, UpdateWorkflowDTO workflowDTO) {
        validationUtil.validate(workflowDTO);
        String tenantId = SecurityUtils.getTenantId();
        Workflow existingWorkflow= workflowRepository.findByTentantAndWorkflowId(tenantId, workflowId)
                .orElseThrow(() -> new RestApiException("Workflow not found", HttpStatus.NOT_FOUND));

        existingWorkflow.setWorkflowId(workflowDTO.getWorkflowId());
        existingWorkflow.setTenantId(tenantId);
        existingWorkflow.setName(workflowDTO.getName());
        existingWorkflow.setDescription(workflowDTO.getDescription());
        existingWorkflow.setStatus(workflowDTO.getStatus());
        existingWorkflow.setVersion(workflowDTO.getVersion());
        existingWorkflow.setProcessId(workflowDTO.getProcesses());
        existingWorkflow.setMetadata(workflowDTO.getMetadata());

        workflowRepository.save(existingWorkflow);
        return ResponseUtil.getResponseMessage("Workflow updated successfully");
    }


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
                .departmentId(dto.getDepartmentId())
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