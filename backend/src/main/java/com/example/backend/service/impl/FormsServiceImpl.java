package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.FormFieldsDTO;
import com.example.backend.dto.FormsDTO;
import com.example.backend.model.Form;
import com.example.backend.model.FormField;
import com.example.backend.repository.FormRepository;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.FormsService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormsServiceImpl implements FormsService {
    private final FormRepository  formRepository;
    private final PagebleObject pagebleObject;
    private final ValidationUtil  validationUtil;
    private final Validator validator;


    @Override
    public ApiResponse<FormsDTO> createForms(FormsDTO formsDTO) {
        validationUtil.validate(formsDTO);
        List<FormField> formFields= pagebleObject.mapList(
                formsDTO.getFields()
                        .stream()
                        .map(this::validateFromFields)
                        .collect(Collectors.toList()), FormField.class);
        System.out.println(formFields.toString());
        Form form=pagebleObject.map(formsDTO,Form.class);
        form.setId(null);
        form.setFields(formFields);
        formRepository.save(form);
        return ResponseUtil.getResponse(formsDTO, "Form created");
    }


    private FormFieldsDTO validateFromFields(FormFieldsDTO fieldsDTO) {
        Set<ConstraintViolation<FormFieldsDTO>> violations = validator.validate(fieldsDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return fieldsDTO;
    }
}
