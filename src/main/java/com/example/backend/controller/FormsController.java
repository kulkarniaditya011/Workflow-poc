package com.example.backend.controller;

import com.example.backend.annotations.AdminApi;
import com.example.backend.dto.CreateFormDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<String>> createForm( @Valid @RequestBody CreateFormDTO formsDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formsService.createForms(formsDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a form by form id")
    @PreAuthorize("hasAuthority('READ_FORM')")
    public ResponseEntity<ApiResponse<FormsDTO>> getFormByFormId(@PathVariable("id") String formId) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByFormId(formId));

    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Form using form id")
    @AdminApi
    @PreAuthorize("hasAuthority('UPDATE_FORM')")
    public ResponseEntity<ApiResponse<String>> updateForm(@Parameter(schema = @Schema(implementation = FormsDTO.class))
                                                              @RequestPart(value = "FormDto", required = false) String payload,
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
    public ResponseEntity<ApiResponse<List<FormsDTO>>> getAllForms() {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getAllForms());
    }


    @GetMapping("/departments/id")
    @Operation(summary = "Returns all forms in a department")
    @PreAuthorize("hasAuthority('READ_FORM')")
    public ResponseEntity<ApiResponse<List<FormsDTO>>> getFormsByDepartments(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(formsService.getFormsByDepartment(id));
    }

}
