package com.example.backend.controller;

import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
public class FormsController {

    private final FormsService formsService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createForm( @Valid @RequestBody CreateFormDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formsService.createForms(formsDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FormsDTO>> getFormByFormId(@RequestParam String formId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formsService.getFormsByFormId(formId));

    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateForm(@Valid @RequestBody FormsDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteForm(@RequestParam String formId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }
}
