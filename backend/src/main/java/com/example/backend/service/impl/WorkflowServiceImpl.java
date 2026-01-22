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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {
    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;

    @Override
    public ApiResponse<String> createWorkflow(CreateWorkflowDTO createWorkflowDTO) {
        validationUtil.validate(createWorkflowDTO);
        Map<String, Object> filter = Map.of("workflowId", createWorkflowDTO.getWorkflowId());

        if (restheartService.getWithFilter("workflows", filter)
                .map(map -> pagebleObject.convertValue(map, Workflow.class))
                .blockFirst() != null) {
            throw new RestApiException(String.format("Workflow with id: %s already exists",
                    createWorkflowDTO.getWorkflowId()),
                    HttpStatus.BAD_REQUEST);
        }

        Workflow workflow= Workflow.builder()
                .tenantId(createWorkflowDTO.getTenantId())
                .workflowId(createWorkflowDTO.getWorkflowId())
                .name(createWorkflowDTO.getName())
                .description(createWorkflowDTO.getDescription())
                .status(createWorkflowDTO.getStatus())
                .processId(createWorkflowDTO.getProcessId())
                .metadata(createWorkflowDTO.getWorkflowMetadata())
                .build();
        log.info("Workflow object:{}", workflow.toString());
        restheartService.create("workflows", workflow, Workflow.class).block();
        return ResponseUtil.getResponseMessage(String.format("Workflow created successfully with id: %s", createWorkflowDTO.getWorkflowId()));
    }
}
