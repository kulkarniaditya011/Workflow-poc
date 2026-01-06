package com.example.backend.controller;

import com.example.backend.dto.FormsDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormsController {

    private final FormsService formsService;

    @PostMapping("/create-forms")
    public ResponseEntity<ApiResponse<FormsDTO>> createForm( @Valid @RequestBody FormsDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.createForms(formsDTO));
    }

}
