package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormResponseDTO;
import com.example.backend.dto.UpdateFormDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forms")
@RequiredArgsConstructor
@Tag(name = "Forms", description = "Workflow form APIs")
public class FormsController {

    private final FormsService formsService;

    @PostMapping
    @Operation(summary = "Create a Form")
    @AdminApi
    @PreAuthorize("hasAuthority('CREATE_FORM')")
    public ResponseEntity<ApiResponse<String>> createForm(@Valid @RequestBody CreateFormDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formsService.createForms(formsDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a form by form id")
    @PreAuthorize("hasAuthority('READ_FORM')")
    public ResponseEntity<ApiResponse<FormResponseDTO>> getFormByFormId(@PathVariable("id") String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByFormId(formId));

    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Form using form id")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_FORM')")
    public ResponseEntity<ApiResponse<String>> updateForm(@RequestBody UpdateFormDTO payload,
                                                              @PathVariable("id") String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.updateForm(payload, formId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Form by form id")
    @AdminApi
    @PreAuthorize("hasAuthority('DELETE_FORM')")
    public ResponseEntity<ApiResponse<String>> deleteForm(@PathVariable("id") String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.deleteForms(formId));
    }

    @GetMapping
    @Operation(summary = "Get all Forms")
    @PreAuthorize("hasAuthority('READ_FORM')")
    public ResponseEntity<ApiResponse<Page<FormResponseDTO>>> getAllForms(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "name") String sortBy,
                                                                          @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getAllForms(page, size, sortBy, direction));
    }


    @GetMapping("/departments/{id}")
    @Operation(summary = "Returns all forms in a department")
    @PreAuthorize("hasAuthority('READ_FORM')")
    public ResponseEntity<ApiResponse<Page<FormResponseDTO>>> getFormsByDepartments(@PathVariable("id") String id,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestParam(defaultValue = "name") String sortBy,
                                                                             @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByDepartment(id, page, size,sortBy ,direction));
    }

    @PatchMapping("approve/{id}")
    @Operation(summary = "Approves a form")
        @PreAuthorize("hasAuthority('APPROVE_FORM')")
    public ResponseEntity<ApiResponse<String>> approveForm(@PathVariable("id") String formId, @RequestBody String comment) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.approveForm(formId, comment));
    }

    @PatchMapping("/reject/{id}")
    @Operation(summary = "Reject a form")
    @PreAuthorize("hasAuthority('REJECT_FORM')")
    public ResponseEntity<ApiResponse<String>> rejectForm(@PathVariable("id") String formId, @RequestBody String reason){
        return ResponseEntity.status(HttpStatus.OK).body(formsService.rejectForm(formId, reason));
    }

}
