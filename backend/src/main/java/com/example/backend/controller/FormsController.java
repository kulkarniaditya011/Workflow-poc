package com.example.backend.controller;

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

    @PostMapping("/create-forms")
    public ResponseEntity<ApiResponse<String>> createForm( @Valid @RequestBody CreateFormDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.createForms(formsDTO));
    }

    @GetMapping("/getFormByFormId")
    public ResponseEntity<ApiResponse<FormsDTO>> getFormByFormId(@RequestParam String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByFormId(formId));
    }

}
