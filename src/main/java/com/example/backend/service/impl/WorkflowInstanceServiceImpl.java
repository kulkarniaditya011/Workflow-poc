package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.RequestWorkflowInstanceDTO;
import com.example.backend.dto.WorkflowDTO;
import com.example.backend.enums.ResourceStatus;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.*;
import com.example.backend.model.Process;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.repository.WorkflowInstanceRepository;
import com.example.backend.repository.WorkflowRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowInstanceService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService {

    private final ValidationUtil validationUtil;
    private final WorkflowInstanceRepository workflowInstanceRepository;
    private final WorkflowRepository workflowRepository;
    private final ProcessRepository processRepository;
    private final PagebleObject pagebleObject;

    @Override
    public ApiResponse<String> startWorkflow(String id, RequestWorkflowInstanceDTO instance) {
        validationUtil.validate(instance);
        String tenantId= SecurityUtils.getTenantId();

        Workflow workflow= getWorkflow(tenantId, id);
        ApprovalMetadata approval = workflow.getApproval();
        log.info(approval.toString());

        if(!ResourceStatus.APPROVED.equals(approval.getStatus())){
            throw new RestApiException("This Workflow is not approved yet", HttpStatus.BAD_REQUEST);
        }


        List<Process> processes= getProcesses(tenantId,workflow.getProcessId());
        List<StepDefinition> allSteps= getOrderedSteps(processes);

        if(allSteps.isEmpty()){
            throw new RestApiException("Workflow has no allSteps", HttpStatus.BAD_REQUEST);
        }
        WorkflowInstance workflowInstance= buildInstance(instance, tenantId, workflow, allSteps);
        workflowInstanceRepository.save(workflowInstance);
        return ResponseUtil.getResponseMessage(String.format("Workflow Started. Instance Id: %s", workflowInstance.getInstanceId()));
    }



    @Override
    @Transactional
    public ApiResponse<String> completeSteps(String id, String stepKey) {
        String tenantId= SecurityUtils.getTenantId();
        WorkflowInstance instance= getInstance(tenantId,id);

        if(!ResourceStatus.IN_PROGRESS.equals(instance.getStatus())){
            throw new RestApiException( "Workflow instance is not in progress", HttpStatus.BAD_REQUEST );
        }

    if(!stepKey.equals(instance.getCurrentStepId())){
        throw new RestApiException("Invalid step execution order. Current step is: " +
                instance.getCurrentStepId(),
                HttpStatus.BAD_REQUEST );
    }
        List<Process> processes = getProcesses(tenantId,instance.getProcessId());

        List<StepDefinition> orderedSteps= getOrderedSteps(processes);
        if (orderedSteps.isEmpty()) {
            throw new RestApiException("Workflow has no steps", HttpStatus.BAD_REQUEST);
        }
        log.info("all steps: {}", orderedSteps);

        Map<String, Integer> stepIndexMap = IntStream.range(0, orderedSteps.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> orderedSteps.get(i).getStepKey(),
                        i -> i
                ));
        int currIndex= stepIndexMap.getOrDefault(stepKey, -1);

        if(currIndex==-1){
            throw new RestApiException("Step not found in workflow definition", HttpStatus.BAD_REQUEST );
        }

        if(currIndex+1<orderedSteps.size()){
            instance.setCurrentStepId(orderedSteps.get(currIndex+1).getStepKey());
        }
        else{
            instance.setCurrentStepId(null);
            instance.setStatus(ResourceStatus.COMPLETED);
        }

        instance.getMetadata().setUpdatedAt(LocalDateTime.now());
        workflowInstanceRepository.save(instance);
        return ResponseUtil.getResponseMessage(
                ResourceStatus.COMPLETED.equals(instance.getStatus())
                        ? "Workflow completed successfully"
                        : "Step completed successfully. Moved to next step"
        );
    }

    @Override
    public ApiResponse<String> stopWorkflow(String workflowInstanceId) {
        String tenantId= SecurityUtils.getTenantId();
        WorkflowInstance instance= getInstance(tenantId,workflowInstanceId);
        if (ResourceStatus.COMPLETED.equals(instance.getStatus())) {
            throw new RestApiException(
                    "Completed workflow cannot be stopped",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (ResourceStatus.CLOSED.equals(instance.getStatus())) {
            throw new RestApiException(
                    "Workflow is already stopped",
                    HttpStatus.BAD_REQUEST
            );
        }

        //  Stop the workflow
        instance.setStatus(ResourceStatus.CLOSED);
        instance.setCurrentStepId(null);
        instance.getMetadata().setUpdatedAt(LocalDateTime.now());

        workflowInstanceRepository.save(instance);
        return ResponseUtil.getResponseMessage("Workflow Stopped successfully");
    }

    @Override
    public ApiResponse<Page<WorkflowDTO>> getAllRunningWorkflows(int page, int size, String sortBy, String direction) {
        String tenantId= SecurityUtils.getTenantId();
        Sort sort= direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WorkflowInstance> runningInstances =
                workflowInstanceRepository.findByTenantIdAndStatus(tenantId, ResourceStatus.IN_PROGRESS, pageable);
        if (runningInstances.isEmpty()) {
            return ResponseUtil.getResponse(Page.empty(pageable), "No workflows currently running");
        }

        List<String> workflowIds = runningInstances.getContent().stream()
                .map(WorkflowInstance::getWorkflowId)
                .distinct()
                .collect(Collectors.toList());
        List<WorkflowDTO> workflows = workflowRepository
                .findByTenantIdAndWorkflowIdIn(tenantId, workflowIds)
                .stream()
                .map(workflow -> pagebleObject.map(workflow, WorkflowDTO.class))
                .toList();
        Page<WorkflowDTO> response =
                new PageImpl<>(workflows, pageable, runningInstances.getTotalElements());
        return ResponseUtil.getResponse(response, "Workflows retrieved successfully");
    }

    private WorkflowInstance getInstance(String tenantId,String instanceId){
        return workflowInstanceRepository.findByTenantAndInstanceId(tenantId, instanceId)
                .orElseThrow(()-> new RestApiException("Workflow instance not found", HttpStatus.NOT_FOUND));
    }

    private Workflow getWorkflow(String tenantId, String workflowId){
        return workflowRepository.findByTenantIdAndWorkflowId(tenantId,workflowId)
                .orElseThrow(()-> new RestApiException("Workflow not found", HttpStatus.NOT_FOUND));
    }

    private List<Process> getProcesses(String tenantId, List<String> processId){
       return processRepository.findByTenantIdAndProcessIdIn(tenantId, processId, Pageable.unpaged()).getContent();
    }

    private List<StepDefinition> getOrderedSteps(List<Process> processes){
        return processes.stream()
                .flatMap(process-> process.getSteps().stream())
                .sorted(Comparator.comparing(StepDefinition::getOrder))
                .toList();
    }

    private WorkflowInstance buildInstance(RequestWorkflowInstanceDTO instance, String tenantId, Workflow workflow, List<StepDefinition> allSteps) {
        StepDefinition firstStep = allSteps.get(0);
        return  WorkflowInstance.builder()
                .instanceId(
                        instance.getInstanceId() !=null
                                ?instance.getInstanceId(): UUID.randomUUID().toString()
                )
                .workflowId(workflow.getWorkflowId())
                .tenantId(tenantId)
                .processId(workflow.getProcessId())
                .currentStepId(firstStep.getStepKey())
                .status(ResourceStatus.IN_PROGRESS)
                .metadata(InstanceMetadata
                        .builder()
                        .createdAt(LocalDateTime.now())
                        .totalSteps(allSteps.size())
                        .build())
                .build();

    }

}
