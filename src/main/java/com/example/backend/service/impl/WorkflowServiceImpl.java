package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.UpdateWorkflowDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.enums.ResourceStatus;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.Process;
import com.example.backend.model.Workflow;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.repository.WorkflowRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RestheartService;
import com.example.backend.service.WorkflowService;
import com.example.backend.utilService.SecurityUtils;
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
    private final DepartmentsRepository departmentsRepository;
    private final ProcessRepository processRepository;
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
    public ApiResponse<Page<ProcessDTO>> getProcessForWorkflow(String workflowId, int page, int size, String sortBy, String direction) {
        String tenantId = SecurityUtils.getTenantId();
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Workflow workflow= workflowRepository.findByTenantIdAndWorkflowId(tenantId, workflowId)
                .orElseThrow(()-> new RestApiException(
                        String.format("Workflow with id: %s not found", workflowId),
                        HttpStatus.NOT_FOUND)
                );
        Page<ProcessDTO> processes= processRepository.findByTenantIdAndProcessIdIn(tenantId, workflow.getProcessId(), pageable)
                .map(process -> pagebleObject.map(process, ProcessDTO.class));

       if (!processes.hasContent()) {
           throw new RestApiException(
                   String.format("Workflow with id: %s has no process", workflowId),
                   HttpStatus.NOT_FOUND);
       }
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
    public ApiResponse<Page<WorkflowDTO>> getAllWorkflows(int page, int size, String sortBy, String direction) {
        String tenantId= SecurityUtils.getTenantId();
        Sort sort = direction.equalsIgnoreCase("desc")
                ?Sort.by(sortBy).descending()
                :Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WorkflowDTO> workflows= workflowRepository.findByTenantId(tenantId,pageable)
                .map(workflow -> pagebleObject.map(workflow, WorkflowDTO.class));
        if (!workflows.hasContent()) {
            throw new RestApiException("Workflows not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(workflows, "Workflows retrieved successfully");
    }

    @Override
    public ApiResponse<Page<WorkflowDTO>> getWorkflowByDepartment(String departmentId, int page, int size, String sortBy, String direction) {
        String tenantId= SecurityUtils.getTenantId();

        Sort sort = direction.equalsIgnoreCase("desc")
                ?Sort.by(sortBy).descending()
                :Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if(doesDepartmentExists(departmentId, tenantId)){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        Page<WorkflowDTO> workflows= workflowRepository.findByTenantIdAndDepartmentId(tenantId,departmentId,pageable)
                .map(workflow -> pagebleObject.map(workflow, WorkflowDTO.class));

        if (!workflows.hasContent()) {
            throw new RestApiException("Workflows not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(workflows, "Workflow retrieved successfully");

    }

    @Override
    public ApiResponse<String> updateWorkflow(String workflowId, UpdateWorkflowDTO workflowDTO) {
        validationUtil.validate(workflowDTO);
        String tenantId = SecurityUtils.getTenantId();
        Workflow existingWorkflow= workflowRepository.findByTenantIdAndWorkflowId(tenantId, workflowId)
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

    @Override
    public ApiResponse<String> approveWorkflow(String workflowId, String comment) {
        String tenantId = SecurityUtils.getTenantId();
        String approver = SecurityUtils.getUsername();

        Workflow workflow = workflowRepository
                .findByTenantIdAndWorkflowId(tenantId, workflowId)
                .orElseThrow(() ->
                        new RestApiException("Workflow not found", HttpStatus.NOT_FOUND)
                );

        ApprovalMetadata approval = workflow.getApproval();

        if (approval == null) {
            throw new RestApiException(
                    "Approval metadata not found for workflow",
                    HttpStatus.BAD_REQUEST
            );
        }

        validateApprovedProcess(workflow.getProcessId(), tenantId);

        if (approval.getStatus() == ResourceStatus.APPROVED) {
            return ResponseUtil.getResponseMessage("Workflow already approved");
        }

        if (approval.getStatus() != ResourceStatus.PENDING) {
            return ResponseUtil.getResponseMessage(
                    "Workflow cannot be approved from state: " + approval.getStatus()
            );
        }

        approval.setStatus(ResourceStatus.APPROVED);
        approval.setActionBy(approver);
        approval.setActionAt(Instant.now());
        approval.setComment(comment);

        workflowRepository.save(workflow);

        return ResponseUtil.getResponseMessage("Workflow approved successfully");
    }

    @Override
    public ApiResponse<String> rejectWorkflow(String workflowId, String reason) {
        String tenantId= SecurityUtils.getTenantId();
        String approver= SecurityUtils.getUsername();

        Workflow workflow= workflowRepository.findByTenantIdAndWorkflowId(tenantId, approver)
                .orElseThrow(() -> new RestApiException("Workflow not found", HttpStatus.NOT_FOUND));
        ApprovalMetadata approval= workflow.getApproval();
        validateRejectState(approval);
        if (approval.getStatus() == ResourceStatus.REJECTED) {
            return ResponseUtil.getResponseMessage("Workflow already rejected");
        }
        approval.setStatus(ResourceStatus.REJECTED);
        approval.setActionBy(approver);
        approval.setActionAt(Instant.now());
        approval.setComment(reason);

        workflowRepository.save(workflow);
        return ResponseUtil.getResponseMessage("Workflow rejected successfully");
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
        ApprovalMetadata approval= new ApprovalMetadata();
        approval.setStatus(ResourceStatus.PENDING);
        return Workflow.builder()
                .tenantId(tenantId)
                .workflowId(dto.getWorkflowId())
                .name(dto.getName())
                .description(dto.getDescription())
                .departmentId(dto.getDepartmentId())
                .status(dto.getStatus())
                .approval(approval)
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
    private boolean doesDepartmentExists(String departmentId, String tenantId){
       return departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty();
    }

    private void validateApprovedProcess(List<String> processId, String tenantId) {
        List<Process> processes = processRepository.findByTenantIdAndProcessIdIn(tenantId, processId, Pageable.unpaged())
                .getContent();
        if (processes.isEmpty()) {
            throw new RestApiException("Process not found", HttpStatus.NOT_FOUND);
        }

        processes.forEach(process -> {
            ApprovalMetadata approval = process.getApproval();
            if (approval == null || approval.getStatus() != ResourceStatus.APPROVED){
            throw new RestApiException(String.format("Process with id: %s is not approved", process.getProcessId()), HttpStatus.BAD_REQUEST);
        }});
    }
    private void validateRejectState(ApprovalMetadata approval) {

        if (approval == null) {
            throw new RestApiException("Approval metadata not found", HttpStatus.BAD_REQUEST);
        }

        if (approval.getStatus() == ResourceStatus.APPROVED) {
            throw new RestApiException("Approved workfflow cannot be rejected", HttpStatus.BAD_REQUEST);
        }
    }
}