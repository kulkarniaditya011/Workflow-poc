package com.example.backend.model;

import com.example.backend.enums.StepType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StepDefinition {

    private String stepKey;
    private String name;
    private Integer order;
    private StepType type;
    private Assignment assignment;
    private String formId;

}
