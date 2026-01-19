package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/register")
@RequiredArgsConstructor
@Tag(name = "Register", description = "Register a user")
@RestController
public class RegisterController {
    private final AuthenticationService authService;

    @PostMapping
    @AdminApi
    public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignUpRequest signUpRequest, @RequestHeader("registration-for") String resgistrationFor){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signUpRequest, resgistrationFor));

    }

}
