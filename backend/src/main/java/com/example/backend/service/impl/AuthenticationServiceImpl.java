package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.SignInRequest;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Users;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.AuthenticationService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.JwtService;
import com.example.backend.utilService.SecurityUser;
import com.example.backend.utilService.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final ValidationUtil validationUtil;
    private final RestheartService restheartService;
    private final PasswordEncoder passwordEncoder;
    private final PagebleObject pagebleObject;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityUtils securityUtils;


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
        Users user = restheartService.create(
                        "users",
                        buildUser(signUpRequest),
                        Users.class
                )
                .block();

        log.info("User registered: {}", user.getEmail());

        return ResponseUtil.getResponseMessage("User registered successfully");
    }

    @Override
    public ApiResponse<Map<String, String>> login(SignInRequest signInRequest) {
       Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getEmail(),
                            signInRequest.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new RestApiException(
                    "Invalid email or password",
                    HttpStatus.UNAUTHORIZED
            );
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtService.generateToken(securityUser));
        tokens.put("refreshToken", jwtService.refreshToken(securityUser));

        return ResponseUtil.getResponse(tokens, "Login successful");

    }

    private Users buildUser(SignUpRequest signUpRequest) {

        if (signUpRequest.getRoles().isEmpty()){
            throw new RestApiException("Roles cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return Users.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(signUpRequest.getRoles())
                .tenantId(SecurityUtils.getTenantId())
                .build();
    }

}
