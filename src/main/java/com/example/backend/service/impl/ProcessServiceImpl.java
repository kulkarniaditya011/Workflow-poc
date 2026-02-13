package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.*;
import com.example.backend.enums.ResourceStatus;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.ApprovalMetadata;
import com.example.backend.model.Process;
import com.example.backend.model.StepDefinition;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {



    private final ValidationUtil validationUtil;
    private final DepartmentsRepository departmentsRepository;
    private final PagebleObject pagebleObject;
    private final RestheartService restheartService;
    private final ProcessRepository processRepository;

    // ========================= CREATE =========================

    @Override
    public ApiResponse<String> createProcess(RequestProcessDTO dto) {

        validationUtil.validate(dto);
        String tenantId = SecurityUtils.getTenantId();
        ensureProcessDoesNotExist(dto.getProcessId(), tenantId);

        List<StepDefinition> steps = validateAndMapSteps(dto.getSteps());

        Process process = Process.builder()
                .tenantId(tenantId)
                .processId(dto.getProcessId())
                .name(dto.getName())
                .description(dto.getDescription())
                .departmentId(dto.getDepartmentId())
                .status(ResourceStatus.PENDING)
                .executionPattern(dto.getExecutionPattern())
                .assignment(dto.getDefaultAssignment())
                .approval(ApprovalMetadata.builder().status(ResourceStatus.PENDING).build())
                .steps(steps)
                .build();
        log.info("Creating process: {}", process);
        restheartService
                .create("process", process, Process.class)
                .block();

        return ResponseUtil.getResponseMessage(
                "Process created with id: " + dto.getProcessId()
        );
    }




    @Override
    public ApiResponse<String> updateProcess(UpdateProcessDTO dto, String processId) {

        validationUtil.validate(dto);
        String tenantId = SecurityUtils.getTenantId();

        Process process = findProcess(processId, tenantId);
    ApprovalMetadata approval = dto.getApproval();

        List<StepDefinition> steps = validateAndMapSteps(dto.getSteps());
        process.setTenantId(tenantId);
        process.setProcessId(dto.getProcessId());
        process.setName(dto.getName());
        process.setDescription(dto.getDescription());
        process.setDepartmentId(dto.getDepartmentId());
        process.setStatus(dto.getApproval().getStatus());
        process.setExecutionPattern(dto.getExecutionPattern());
        process.setAssignment(dto.getAssignment());
        process.setApproval(approval);
        process.setSteps(steps);
        processRepository.save(process);

        return ResponseUtil.getResponseMessage("Process updated.");
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
    public ApiResponse<Page<ProcessDTO>> getAllProcesses(int page, int size, String sortBy, String direction) {

        String tenantId = SecurityUtils.getTenantId();
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProcessDTO> processes= processRepository.findByTenantId(tenantId, pageable)
                .map(process-> pagebleObject.map(process, ProcessDTO.class));
        if (!processes.hasContent()) {
            throw new RestApiException("No processes found", HttpStatus.NOT_FOUND);
        }

        return ResponseUtil.getResponse(processes, "Processes retrieved successfully");
    }

    @Override
    public ApiResponse<Page<ProcessDTO>> getProcessByDepartment(String departmentId, int page, int size, String sortBy, String direction) {
        String tenantId= SecurityUtils.getTenantId();
        if(doesDepartmentExists(tenantId, departmentId)){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
       Page<ProcessDTO> processDTOs= processRepository.findByTenantIdAndDepartmentId(tenantId, departmentId, pageable)
               .map(process -> pagebleObject.map(process, ProcessDTO.class));
        if (!processDTOs.hasContent()) {
            throw new RestApiException("No processes found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(processDTOs, "Processes retrieved successfully");
    }

    @Override
    @Transactional
    public ApiResponse<String> approveProcess(String processId, String comment) {
        String tenantId= SecurityUtils.getTenantId();
        Process process = processRepository.findByTenantIdAndProcessId(tenantId, processId)
                .orElseThrow(() -> new RestApiException("No process found", HttpStatus.NOT_FOUND));

        ApprovalMetadata approval= process.getApproval();
        if (approval == null) {
            throw new RestApiException(
                    "Approval metadata not found for process",
                    HttpStatus.BAD_REQUEST
            );
        }

        if(approval.getStatus().equals(ResourceStatus.APPROVED)){
            return ResponseUtil.getResponseMessage("Process already approved");
        }
        if (!approval.getStatus().equals(ResourceStatus.PENDING)) {
            return ResponseUtil.getResponseMessage("Process maybe rejected");
        }
        approval.setStatus(ResourceStatus.APPROVED);
        approval.setActionBy(SecurityUtils.getUsername());
        approval.setActionAt(Instant.now());
        approval.setComment(comment);

        processRepository.save(process);
        return ResponseUtil.getResponseMessage("Process approved!!");
    }

    @Override
    public ApiResponse<String> rejectProcess(String processId, String reason) {
        String tenantId = SecurityUtils.getTenantId();
        String approver = SecurityUtils.getUsername();


        Process process = processRepository
                .findByTenantIdAndProcessId(tenantId, processId)
                .orElseThrow(() ->
                        new RestApiException("No process found", HttpStatus.NOT_FOUND)
                );

        ApprovalMetadata approval = process.getApproval();

        validateRejectState(approval);
        if (approval.getStatus() == ResourceStatus.REJECTED) {
            return ResponseUtil.getResponseMessage("Process already rejected");
        }

        approval.setStatus(ResourceStatus.REJECTED);
        approval.setActionBy(approver);
        approval.setActionAt(Instant.now());
        approval.setComment(reason);

        processRepository.save(process);

        return ResponseUtil.getResponseMessage("Process rejected successfully");
    }

    @Override
    public ApiResponse<Page<ResponseStepDTO>> getStepsByProcess(String processId, int page, int size, String sortBy, String direction) {
        String tenantId = SecurityUtils.getTenantId();

        Process process = processRepository
                .findByTenantIdAndProcessId(tenantId, processId)
                .orElseThrow(() ->
                        new RestApiException("No process found", HttpStatus.NOT_FOUND)
                );

        log.info("Fetching steps for processId={}", processId);

        List<ResponseStepDTO> steps = process.getSteps().stream()
                .map(step -> pagebleObject.map(step, ResponseStepDTO.class))
                .collect(Collectors.toList());

        // Safe defaults
        page = Math.max(page, 0);
        size = size <= 0 ? 10 : Math.min(size, 100);

        // Sorting
        Comparator<ResponseStepDTO> comparator = getSortedSteps(sortBy);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        steps.sort(comparator);

        // Pagination
        int total = steps.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);

        List<ResponseStepDTO> pagedSteps = steps.subList(fromIndex, toIndex);

        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseStepDTO> result =
                new PageImpl<>(pagedSteps, pageable, total);

        return ResponseUtil.getResponse(result, "Steps retrieved successfully");
    }

    // ========================= DELETE =========================

    @Override
    public ApiResponse<String> deleteProcess(String processId) {
        String tenantId = SecurityUtils.getTenantId();
        Process process = processRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RestApiException("No process found", HttpStatus.NOT_FOUND));
        processRepository.delete(process);
        return ResponseUtil.getResponseMessage("Process deleted successfully");
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

    private void ensureProcessDoesNotExist(String processId, String tenantId) {

        if (processRepository.findByTenantIdAndProcessId(tenantId, processId).isPresent()) {
            throw new RestApiException(
                    "Process already exists with id: " + processId,
                    HttpStatus.BAD_REQUEST
            );
        }
    }


    private Process findProcess(String processId, String tenantId) {
        return processRepository.findByTenantIdAndProcessId(tenantId, processId)
                .orElseThrow(() -> new RestApiException("No process found", HttpStatus.NOT_FOUND));
    }

    private boolean doesDepartmentExists(String tenantId, String departmentId) {
       return departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty();
    }

    private Comparator<ResponseStepDTO> getSortedSteps(String sortBy) {
        return switch (sortBy) {
            case "name"-> Comparator.comparing(ResponseStepDTO::getName,
                    Comparator.nullsLast(String::compareToIgnoreCase));
            case "order"-> Comparator.comparing(ResponseStepDTO::getOrder,
                    Comparator.nullsLast(Integer::compareTo));
            default -> Comparator.comparing(ResponseStepDTO::getStepKey,
                    Comparator.nullsLast(String::compareToIgnoreCase));
        };
    }


    private void validateRejectState(ApprovalMetadata approval) {

        if (approval == null) {
            throw new RestApiException("Approval metadata not found", HttpStatus.BAD_REQUEST);
        }

        if (approval.getStatus() == ResourceStatus.APPROVED) {
            throw new RestApiException("Approved process cannot be rejected", HttpStatus.BAD_REQUEST);
        }
    }

}
