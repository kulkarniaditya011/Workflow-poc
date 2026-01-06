package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StepsDTO {
    private String stepId;
    private String name;
    private Integer sequence;
    private String type;
    private List<String> assignedRoles;
    private List<String> assignedUsers;

    private String formId;
}
