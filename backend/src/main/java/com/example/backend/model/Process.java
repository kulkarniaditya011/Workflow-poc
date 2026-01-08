package com.example.backend.model;

import com.example.backend.common.ObjectIdDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Process {

    @Id
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

