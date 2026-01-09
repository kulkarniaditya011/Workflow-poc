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

    @GetMapping("/{formId}")
    public ResponseEntity<ApiResponse<FormsDTO>> getFormByFormId(@PathVariable String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByFormId(formId));

    }

    @PutMapping("/{formId}")
    public ResponseEntity<ApiResponse<String>> updateForm(@Valid @RequestBody FormsDTO formsDTO, @PathVariable String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping("/{formId}")
    public ResponseEntity<ApiResponse<String>> deleteForm(@PathVariable String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @PostMapping("/submit/{formId}")
    public ResponseEntity<ApiResponse<String>> submitForm(@PathVariable String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

}
