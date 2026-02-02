package com.example.backend.dto;

import com.example.backend.model.Assignment;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StepsDTO {
    @NotEmpty(message = "step id cannot be empty")
    private String stepKey;
    private String name;
    private Integer order;
    private String type;
    private Assignment assignment;
    private String formId;
}
