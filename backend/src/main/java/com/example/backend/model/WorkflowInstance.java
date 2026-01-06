package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "workflowInstances")
@Builder
public class WorkflowInstance {

    @Id
    @JsonProperty("_id")
    private String id;

    private String instanceId;
    private String workflowId;

    private String tenantId;
    private Map<String, Object> properties;
    /* properties:
    "data": {  // ← John's actual data
    "loanAmount": 50000,
    "applicantName": "John Doe",
    "creditScore": 720
  } */


    // Reference to the entity being processed
    private String entityName; // "loan_application"
    private String entityId; // "LOAN-2024-00123"

    // Current workflow state
    private String status;
    private String currentStepId;

    private List<StepHistory> stepHistories;

    @Builder.Default
    private InstanceMetadata metadata= new InstanceMetadata();
}