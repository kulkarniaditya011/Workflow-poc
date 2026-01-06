package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateWorkflowDTO;
import com.example.backend.model.Workflow;
import com.example.backend.repository.WorkflowRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {
    private final ValidationUtil validationUtil;
    private final WorkflowRepository workflowRepository;
    private final PagebleObject pagebleObject;

    @Override
    public ApiResponse<CreateWorkflowDTO> createWorkflow(CreateWorkflowDTO createWorkflowDTO) {
        validationUtil.validate(createWorkflowDTO);
        Workflow workflow= pagebleObject.map(createWorkflowDTO, Workflow.class);
        workflow.setId(null);
        workflowRepository.save(workflow);
        return ResponseUtil.getResponse(createWorkflowDTO, "Workflow created");
    }





}
