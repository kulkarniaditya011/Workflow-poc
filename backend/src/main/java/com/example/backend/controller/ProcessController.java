package com.example.backend.controller;

import com.example.backend.common.ResponseUtil;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
public class ProcessController {
    private final ProcessService processService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createProcess(@Valid @RequestBody ProcessDTO processDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(processService.createProcess(processDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProcessDTO>> getProcess(){
        return ResponseEntity.status(HttpStatus.FOUND).body(ResponseUtil.getResponseMessage("test"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProcessDTO>> updateProcess(@Valid @RequestBody ProcessDTO processDTO){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteProcess(@Valid @RequestBody ProcessDTO processDTO){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }
}
