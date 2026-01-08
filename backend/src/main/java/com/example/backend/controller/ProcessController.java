package com.example.backend.controller;

import com.example.backend.dto.ProcessDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;

    @PostMapping("/create-process")
    public ResponseEntity<ApiResponse<String>> createProcess(@Valid @RequestBody ProcessDTO processDTO){
        return ResponseEntity.status(HttpStatus.OK).body(processService.createProcess(processDTO));
    }
}
