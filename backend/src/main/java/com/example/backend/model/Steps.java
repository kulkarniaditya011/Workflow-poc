package com.example.backend.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Steps {
    private String stepId;
    private String name;
    private Integer sequence;
    private String type; // approval, validate, form_submission, notification

    // Step-level assignment (who must perform this step)
    private List<String> assignedRoles; // e.g., ["APPROVER", "MANAGER"]
    private List<String> assignedUsers; // Specific user IDs (optional, for direct assignment)

    private String formId; // If this step requires form submission

}
