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

    @GetMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessDTO>> getProcess(@PathVariable String processId){
        return ResponseEntity.status(HttpStatus.FOUND).body(ResponseUtil.getResponseMessage("test"));
    }

    @PutMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessDTO>> updateProcess(@Valid @RequestBody ProcessDTO processDTO,
                                                                 @PathVariable String processId){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @DeleteMapping("/{processId}")
    public ResponseEntity<ApiResponse<String>> deleteProcess(@PathVariable String processId){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseUtil.getResponseMessage("test"));
    }

    @PostMapping("/executions/{processId}")
    public ResponseEntity<ApiResponse<String>> executeProcess(@PathVariable String processId){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @GetMapping("next/{processId}")
    public ResponseEntity<ApiResponse<String>> getNextProcess(@PathVariable String processId){
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }

    @GetMapping("/status/{processId}" )
    public ResponseEntity<ApiResponse<String>> getProcessStatus(@PathVariable String processId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUtil.getResponseMessage("test"));
    }
}
