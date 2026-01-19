package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.FormFieldsDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.StepsDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.*;
import com.example.backend.model.Process;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import com.example.backend.service.RestheartService;
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

    @Override
    public ApiResponse<ProcessDTO> updateProcess(String processDTO, String processId) {
        JsonNode jsonNode = pagebleObject.getJsonNode(processDTO);
        ProcessDTO dto = pagebleObject.readValue(processDTO, ProcessDTO.class);

        Map<String, Object> filter = Map.of("processId", processId);
        Process existing = restheartService.getWithFilter("process", filter)
                .map(map -> pagebleObject.convertValue(map, Process.class))
                .blockFirst();

        if (existing == null) {
            throw new RestApiException("Process not found: " + processId, HttpStatus.NOT_FOUND);
        }

        Map<String, Consumer<Object>> updaters = getConsumerMap(existing);

        applyPatch(jsonNode, dto, updaters);

        restheartService.upsert("process", existing.getId(), existing).block();

        return ResponseUtil.getResponseMessage("Process updated");
    }



    private Map<String, Consumer<Object>> getConsumerMap(Process existing) {
        Map<String, Consumer<Object>> updaters = new HashMap<>();

        // Scalars
        updaters.put("processId", v -> existing.setProcessId((String) v));
        updaters.put("WorkflowId", v -> existing.setWorkflowId((String) v));
        updaters.put("processName", v -> existing.setProcessName((String) v));
        updaters.put("sequence", v -> existing.setSequence((Integer) v));
        updaters.put("processType", v -> existing.setProcessType((String) v));
        updaters.put("executionPattern", v -> existing.setExecutionPattern((String) v));

        // Role + User assignment
        updaters.put("assignedRoles", v ->
                existing.setAssignedRoles(
                        pagebleObject.convertValue(v, new TypeReference<List<String>>() {})
                )
        );

        updaters.put("assignedUsers", v ->
                existing.setAssignedUsers(
                        pagebleObject.convertValue(v, new TypeReference<List<String>>() {})
                )
        );

        // Steps (this is critical)
        updaters.put("processSteps", v -> {
            List<StepsDTO> stepDTOs =
                    pagebleObject.convertValue(v, new TypeReference<List<StepsDTO>>() {});

            List<Steps> validated =
                    stepDTOs.stream()
                            .map(validationUtil::validateFields)
                            .map(dto -> pagebleObject.convertValue(dto, Steps.class))
                            .toList();
            existing.setProcessSteps(validated);
        });
        return updaters;
    }

    private void applyPatch(JsonNode jsonNode,
                            ProcessDTO dto,
                            Map<String, Consumer<Object>> fieldUpdaters) {

        fieldUpdaters.forEach((field, updater) -> {
            if (jsonNode.has(field)) {
                JsonNode node = jsonNode.get(field);
                if (!node.isNull()) {
                    Object value = pagebleObject.convertValue(node, Object.class);
                    updater.accept(value);
                }
            }
        });
    }

    @Override
    public ApiResponse<ProcessDTO> getProcessByWorkflow(String workflowId) {
        Map<String, Object> filter = Map.of("workflowId", workflowId);
        log.info("Form id:{}", workflowId);
        ProcessDTO processDTO=restheartService.getWithFilter("process",filter)
                .map(map-> pagebleObject.convertValue(map, Process.class))
                .map(process-> pagebleObject.map(process, ProcessDTO.class))
                .blockFirst();
        log.info("Form :{}", processDTO.toString());

        if (processDTO==null) {
            throw new RestApiException(String.format("Process not found with workflowId: " + workflowId),HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(processDTO, "process retrieved successfully");
    }

    @Override
    public ApiResponse<String> deleteProcess(String processId) {
        Map<String, Object> filter= Map.of("processId", processId);
        Process existing=restheartService.getWithFilter("process", filter)
                .map(map-> pagebleObject.convertValue(map, Process.class))
                .blockFirst();

        if(existing==null){
            throw new RestApiException(String.format("Process not found with id: " + processId),HttpStatus.NOT_FOUND);
        }

        restheartService.delete("process", existing.getId()).block();
        return ResponseUtil.getResponseMessage(String.format("Process with id %s has been deleted", processId));
    }

}
