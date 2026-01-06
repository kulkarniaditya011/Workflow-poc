package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("Process")
@Builder
public class Process {

    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed
    private String processId;
    private String WorkflowId;
    private String processName;
    private Integer sequence;
    private String processType;
    private String executionPattern; //sequential or parallel
    private List<String> assignedRoles;
    private List<String> assignedUsers;
    private List<Steps> processSteps;


}

