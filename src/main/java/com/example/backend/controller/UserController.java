package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.CreateSuperAdminDTO;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.dto.UserDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
@RestController
public class UserController {
    private final UsersService userService;

    @PostMapping
    @AdminApi
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignUpRequest signUpRequest){
        return ResponseEntity.status(HttpStatus.OK).body(userService.signup(signUpRequest));

    }

    @PostMapping("/superAdmin/{id}")
    @AdminApi
    @Operation(summary = "Creates a super-admin")
    @PreAuthorize("hasAuthority('CREATE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> createSuperAdmin(@PathVariable(value = "id") String tenantId,
                                            @Valid @RequestBody CreateSuperAdminDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(userService.createSuperAdmin(request, tenantId));

    }

    @DeleteMapping("/{id}")
    @AdminApi
    @Operation(summary = "Deletes a user")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable(value = "id") String userId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.removeUser(userId));
    }

    @GetMapping("departments/{id}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUserByDepartment(@PathVariable("id") String departmentId){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersDepartments(departmentId));
    }

}
