package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.DepartmentsDTO;
import com.example.backend.dto.RequestDepartmentsDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Departments;
import com.example.backend.model.Users;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.DepartmentsService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentsService {

    private final PagebleObject pagebleObject;
    private final ValidationUtil validationUtil;
    private final UserRepository userRepository;
    private final DepartmentsRepository departmentsRepository;

    @Override
    public ApiResponse<String> createDepartment(RequestDepartmentsDTO request) {
        validationUtil.validate(request);
        if(departmentsRepository.findByTenantIdAndDepartmentId(SecurityUtils.getTenantId(), request.getDepartmentId()).isPresent()){
            throw new IllegalArgumentException("Department with id " + request.getDepartmentId() + " already exists");
        }
        Departments department = buildDepartment(request);
        departmentsRepository.save(department);
        return ResponseUtil.getResponseMessage("Department created successfully");
    }

    @Override
    public ApiResponse<Page<DepartmentsDTO>> getAllDepartments(int page, int size, String sortBy, String direction) {
        Sort sort= direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String tenantId = SecurityUtils.getTenantId();

        Page<DepartmentsDTO> departments= departmentsRepository.findByTenantId(tenantId,pageable)
                .map(dept-> pagebleObject.map(dept, DepartmentsDTO.class));
        if(departments.isEmpty()){
            return ResponseUtil.getResponseMessage("No departments found");
        }

        return ResponseUtil.getResponse(departments, "Departments fetched successfully");
    }

    @Override
    public ApiResponse<String> assignManager(String departmentId, String managerId) {
        String tenantId= SecurityUtils.getTenantId();
        Departments departments = departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department with id " + departmentId + " does not exist"));

        if (departments.getManagerId() != null) {
            throw new RestApiException("Department already has a manager", HttpStatus.CONFLICT);
        }

        Users manager = userRepository.findByUserId(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager does not exist"));

        if (!tenantId.equals(manager.getTenantId())) {
            throw new IllegalArgumentException("Manager does not belong to this tenant");
        }

        departments.setManagerId(managerId);
        departmentsRepository.save(departments);
        return ResponseUtil.getResponseMessage("Manager successfully Added");
    }



    private Departments buildDepartment(RequestDepartmentsDTO request) {
        return Departments.builder()
                .tenantId(SecurityUtils.getTenantId())
                .departmentId(request.getDepartmentId())
                .name(request.getName())
                .description(request.getDescription())
                .managerId(request.getManagerId())
                .build();
    }

}
