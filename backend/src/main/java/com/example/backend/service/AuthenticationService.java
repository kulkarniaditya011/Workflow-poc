package com.example.backend.service;


import com.example.backend.dto.SignInRequest;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthenticationService {

    ApiResponse<String> signup(SignUpRequest signUpRequest);

    ApiResponse<Map<String, String>> login(@Valid SignInRequest signInRequest);
}
