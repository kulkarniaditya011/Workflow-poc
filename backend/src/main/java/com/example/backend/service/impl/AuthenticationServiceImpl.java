package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.SignInRequest;
import com.example.backend.dto.SignUpRequest;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Role;
import com.example.backend.model.Users;
import com.example.backend.repository.RoleRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.AuthenticationService;
import com.example.backend.service.RestheartService;
import com.example.backend.utilService.JwtService;
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
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final ValidationUtil validationUtil;
    private final RestheartService restheartService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PagebleObject pagebleObject;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public ApiResponse<String> signup(SignUpRequest signUpRequest, String resgistrationFor) {
        Map<String, Object> filter = Map.of("email", signUpRequest.getEmail());
       Users restUser= restheartService.getWithFilter("users", filter)
               .map(map-> pagebleObject.convertValue(map, Users.class))
               .blockFirst();
        if (restUser != null) {
            throw new RestApiException("User exists. Please use another mail-id", HttpStatus.BAD_REQUEST);
        }
        validationUtil.validate(signUpRequest);
        Role role = resolveRoles(resgistrationFor);
       Users user= restheartService.create("users", buildUser(signUpRequest, role), Users.class)
                .block();
        log.info("User from restheart: {}",user.toString());
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
        } catch (AuthenticationException e) {
            throw new RestApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        Users user = (Users) authentication.getPrincipal();
        return ResponseUtil.getResponse(getToken(user), "Login successful");

    }

    private Map<String, String> getToken(Users user) {
        return new HashMap<>() {{
            put("accessToken", jwtService.generateToken(user));
            put("refreshToken", jwtService.refreshToken(user));

        }};
    }

    private Users buildUser(SignUpRequest signUpRequest, Role role) {
        return Users.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roleId(role.getId())
                .privilageId(role.getAuthorities())
                .build();

    }

    private Role resolveRoles(String resgistrationFor) {
        if ("ADMIN".equalsIgnoreCase(resgistrationFor)) {
            return roleRepository.findByName(resgistrationFor)
                    .orElseThrow(() -> new RestApiException("Role not found", HttpStatus.NOT_FOUND));
        } else if ("USER".equalsIgnoreCase(resgistrationFor)) {
            return roleRepository.findByName(resgistrationFor)
                    .orElseThrow(() -> new RestApiException("Role not found", HttpStatus.NOT_FOUND));
        }
        throw new RestApiException("Invalid registration role", HttpStatus.BAD_REQUEST);
    }
}
