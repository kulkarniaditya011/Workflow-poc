package com.example.backend.controller;

import com.example.backend.annotations.SharedApi;
import com.example.backend.dto.SignInRequest;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/login")
@RequiredArgsConstructor
@Tag(name = "Login")
@RestController
public class LoginController {

    private final AuthenticationService authService;

    @PostMapping
    @SharedApi
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody SignInRequest signInRequest){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(signInRequest));
    }
}
