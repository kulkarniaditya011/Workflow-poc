package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Process;
import com.example.backend.model.Steps;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import com.example.backend.service.RestheartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {
    private final ProcessRepository processRepository;
    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;


    @Override
    public ApiResponse<String> createProcess(ProcessDTO processDTO) {
        //validating process dto
        validationUtil.validate(processDTO);

        //validating steps
        List<Steps> steps=pagebleObject.mapList(processDTO.getProcessSteps()
                        .stream()
                        .map(validationUtil::validateFields)
                        .toList()
                        ,Steps.class);
        log.info("Steps after validation and mapping {}", steps.toString());

        //checking for existing processes based on process id
        Map<String, Object> filter= Map.of("processId", processDTO.getProcessId());
        if(restheartService.getWithFilter("process", filter)
                .map(obj-> pagebleObject.convertValue(obj, Process.class))
                .blockFirst() !=null){
            throw new RestApiException(String.format("Process with id %s already exists", processDTO.getProcessId()), HttpStatus.BAD_REQUEST);
        }

       Process process= Process.builder()
                .processId(processDTO.getProcessId())
                .WorkflowId(processDTO.getWorkflowId())
                .processName(processDTO.getProcessName())
                .sequence(processDTO.getSequence())
                .processSteps(steps)
                .processType(processDTO.getProcessType())
                .executionPattern(processDTO.getExecutionPattern())
                .assignedRoles(processDTO.getAssignedRoles())
                .assignedUsers(processDTO.getAssignedUsers())
                .build();

        ProcessDTO savedProcess= pagebleObject.map(restheartService.create("process", process, Process.class)
                .block(),ProcessDTO.class);
        log.info("saving dto {}", savedProcess.toString());
        return ResponseUtil.getResponseMessage(String.format("Process with id %s has been created", savedProcess.getProcessId()));
    }



}
