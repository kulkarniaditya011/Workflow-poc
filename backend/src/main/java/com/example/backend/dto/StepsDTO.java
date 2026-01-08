package com.example.backend.dto;

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
    private String stepId;
    private String name;
    private Integer sequence;
    private String type;
    private List<String> assignedRoles;
    private List<String> assignedUsers;

    private String formId;
}
