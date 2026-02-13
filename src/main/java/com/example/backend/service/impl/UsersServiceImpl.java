package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.CreateSuperAdminDTO;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.dto.UserDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Tenant;
import com.example.backend.model.Users;
import com.example.backend.repository.DepartmentsRepository;
import com.example.backend.repository.TenantRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RestheartService;
import com.example.backend.service.UsersService;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final ValidationUtil validationUtil;
    private final RestheartService restheartService;
    private final PagebleObject pagebleObject;
    private final DepartmentsRepository departmentsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;


    @Override
    public ApiResponse<String> signup(SignUpRequest signUpRequest) {
        Map<String, Object> filter = Map.of("email", signUpRequest.getEmail());

        Users existingUser = restheartService.getWithFilter("users", filter)
                .map(map -> pagebleObject.convertValue(map, Users.class))
                .blockFirst();

        if (existingUser != null) {
            throw new RestApiException(
                    "User exists. Please use another mail-id",
                    HttpStatus.BAD_REQUEST
            );
        }
        validationUtil.validate(signUpRequest);
        restheartService.create(
                        "users",
                        buildUser(signUpRequest),
                        Users.class
                )
                .block();

        return ResponseUtil.getResponseMessage("User registered successfully");
    }

    @Override
    public ApiResponse<String> createSuperAdmin(CreateSuperAdminDTO request, String tenantId) {
        validationUtil.validate(request);

        Tenant tenant= tenantRepository.findByTenantId(tenantId)
                .orElseThrow(()-> new RestApiException("Tenant not found", HttpStatus.NOT_FOUND));

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RestApiException("User exists. Please use another mail-id", HttpStatus.BAD_REQUEST);
        }

       userRepository.save(buildSuperAdmin(request, tenant.getTenantId()));
        return ResponseUtil.getResponseMessage("Super Admin registered successfully");
    }

    @Override
    public ApiResponse<String> removeUser(String userId) {
        String tenantId=SecurityUtils.getTenantId();
        Users user= userRepository.findByTenantIdAndUserId(tenantId,userId)
                .orElseThrow(()-> new RestApiException("User not found", HttpStatus.NOT_FOUND));
        userRepository.delete(user);
      return   ResponseUtil.getResponseMessage("User removed successfully");
    }

    @Override
    public ApiResponse<Page<UserDTO>> getUsersDepartments(String departmentId, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String tenantId= SecurityUtils.getTenantId();
        if(departmentsRepository.findByTenantIdAndDepartmentId(tenantId, departmentId).isEmpty()){
            throw new RestApiException("This department does not exists", HttpStatus.BAD_REQUEST);
        }
        Page<UserDTO> users= userRepository.findByTenantIdAndDepartmentId(tenantId, departmentId, pageable)
                .map(user-> pagebleObject.map(user, UserDTO.class));

        if(users.isEmpty()){
            throw new RestApiException("Users not found", HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.getResponse(users, "Users fetched successfully");
    }


    private Users buildUser(SignUpRequest signUpRequest) {

        if (signUpRequest.getRoles().isEmpty()){
            throw new RestApiException("Roles cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return Users.builder()
                .userId(generateUserId())
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(signUpRequest.getRoles())
                .tenantId(SecurityUtils.getTenantId())
                .departmentId(signUpRequest.getDepartmentId())
                .build();
    }

    private String generateUserId(){
        return "usr_"+ UUID.randomUUID();
    }

    private Users buildSuperAdmin(CreateSuperAdminDTO createSuperAdminDTO, String tenantId){
        if (createSuperAdminDTO.getRoles().isEmpty()){
            throw new RestApiException("Roles cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return Users.builder()
                .userId(generateUserId())
                .name(createSuperAdminDTO.getName())
                .email(createSuperAdminDTO.getEmail())
                .password(passwordEncoder.encode(createSuperAdminDTO.getPassword()))
                .roles(createSuperAdminDTO.getRoles())
                .tenantId(tenantId)
                .build();
    }


}
