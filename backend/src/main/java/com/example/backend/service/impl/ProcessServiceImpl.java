package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.model.Process;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.ProcessDTO;
import com.example.backend.repository.ProcessRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {
    private final ProcessRepository processRepository;
    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;


    @Override
    public ApiResponse<ProcessDTO> createProcess(ProcessDTO processDTO) {
        validationUtil.validate(processDTO);
        Process process= pagebleObject.map(processDTO, Process.class);
        processRepository.save(process);
       return ResponseUtil.getResponse(processDTO, "Process created");
    }
}
