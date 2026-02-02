package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.dto.RequestProcessDTO;
import com.example.backend.dto.StepsDTO;
import com.example.backend.enums.ProcessStatus;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Process;
import com.example.backend.model.StepDefinition;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private static final String PROCESS_COLLECTION = "process";

    private final ValidationUtil validationUtil;
    private final DepartmentsRepository departmentsRepository;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;
    private final ProcessRepository processRepository;
    private final ObjectMapper objectMapper;

    // ========================= CREATE =========================

    @Override
    public ApiResponse<String> createProcess(RequestProcessDTO dto) {

        validationUtil.validate(dto);
        String tenantId = SecurityUtils.getTenantId();

        ensureLatestProcessDoesNotExist(dto.getProcessId(), tenantId);

        List<StepDefinition> steps = validateAndMapSteps(dto.getSteps());

        Process process = Process.builder()
                .tenantId(tenantId)
                .processId(dto.getProcessId())
                .name(dto.getName())
                .description(dto.getDescription())
                .departmentId(dto.getDepartmentId())
                .version(1)
                .status(ProcessStatus.DRAFT)
                .latest(true)
                .executionPattern(dto.getExecutionPattern())
                .assignment(dto.getDefaultAssignment())
                .steps(steps)
                .build();
        log.info("Creating process: {}", process);
        restheartService
                .create(PROCESS_COLLECTION, process, Process.class)
                .block();

        return ResponseUtil.getResponseMessage(
                "Process created with id: " + dto.getProcessId()
        );
    }




    @Override
    public ApiResponse<String> updateProcess(RequestProcessDTO dto, String processId) {

        validationUtil.validate(dto);
        String tenantId = SecurityUtils.getTenantId();

        Process latestProcess = findProcess(processId, tenantId);

        // mark old version as not latest
        latestProcess.setLatest(false);
        restheartService
                .upsert(PROCESS_COLLECTION, latestProcess.getId(), latestProcess, Process.class)
                .block();

        List<StepDefinition> steps = validateAndMapSteps(dto.getSteps());
        Process newVersion = Process.builder()
                .tenantId(tenantId)
                .processId(processId)
                .name(dto.getName())
                .description(dto.getDescription())
                .departmentId(dto.getDepartmentId())
                .version(latestProcess.getVersion() + 1)
                .status(ProcessStatus.DRAFT)
                .latest(true)
                .executionPattern(dto.getExecutionPattern())
                .assignment(dto.getDefaultAssignment())
                .steps(steps)
                .build();

        restheartService
                .create(PROCESS_COLLECTION, newVersion, Process.class)
                .block();

        return ResponseUtil.getResponseMessage(
                "Process updated. New version: " + newVersion.getVersion()
        );
    }

    // ========================= GET =========================

    @Override
    public ApiResponse<ProcessDTO> getProcessById(String processId) {

        String tenantId = SecurityUtils.getTenantId();
        Process process = findProcess(processId, tenantId);
        return ResponseUtil.getResponse(
                pagebleObject.map(process, ProcessDTO.class),
                "Process retrieved successfully"
        );
    }

    @Override
    public ApiResponse<List<ProcessDTO>> getAllProcesses() {

        String tenantId = SecurityUtils.getTenantId();

        Map<String, Object> filter = Map.of(
                "tenantId", tenantId,
                "latest", true
        );

        List<ProcessDTO> processes = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(obj -> objectMapper.convertValue(obj, Process.class))
                .map(entity -> pagebleObject.map(entity, ProcessDTO.class))
                .collectList()
                .block();

        return ResponseUtil.getResponse(processes, "Processes retrieved successfully");
    }

    @Override
    public ApiResponse<List<ProcessDTO>> getWorkflowByDepartment(String departmentId) {
        String tenantId= SecurityUtils.getTenantId();
        if(departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty()){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        List<Process> processes= processRepository.findByTenantIdAndDepartmentId(tenantId, departmentId);
        if (processes == null || processes.isEmpty()) {
            throw new RestApiException("Processes not found", HttpStatus.NOT_FOUND);
        }
        List<ProcessDTO> processDTOS= pagebleObject.mapList(processes, ProcessDTO.class);
        return ResponseUtil.getResponse(processDTOS, "Processes retrieved successfully");
    }

    // ========================= DELETE (SOFT) =========================

    @Override
    public ApiResponse<String> deleteProcess(String processId) {

        String tenantId = SecurityUtils.getTenantId();
        Process process = findProcess(processId, tenantId);

        process.setStatus(ProcessStatus.DEPRECATED);

        restheartService
                .upsert(PROCESS_COLLECTION, process.getId(), process, Process.class)
                .block();

        return ResponseUtil.getResponseMessage(
                "Process deprecated successfully"
        );
    }

    // ========================= HELPERS =========================

    private List<StepDefinition> validateAndMapSteps(List<StepsDTO> stepDTOs) {
        return pagebleObject.mapList(
                stepDTOs.stream()
                        .map(validationUtil::validateFields)
                        .toList(),
                StepDefinition.class
        );
    }

    private void ensureLatestProcessDoesNotExist(String processId, String tenantId) {

        Map<String, Object> filter = new HashMap<>();
        filter.put("tenantId", tenantId);
        filter.put("processId", processId);
        filter.put("latest", true);

        Process existing = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(obj -> pagebleObject.convertValue(obj, Process.class))
                .blockFirst();

        if (existing != null) {
            throw new RestApiException(
                    "Process already exists with id: " + processId,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Process findProcess(String processId, String tenantId) {

        Map<String, Object> filter = Map.of(
                "tenantId", tenantId,
                "processId", processId
        );

        Process process = restheartService
                .getWithFilter(PROCESS_COLLECTION, filter)
                .map(obj -> pagebleObject.convertValue(obj, Process.class))
                .blockFirst();
        log.info("Found process: {}", process);
        if (process == null) {
            throw new RestApiException(
                    "Process not found with id: " + processId,
                    HttpStatus.NOT_FOUND
            );
        }

        return process;
    }
}
