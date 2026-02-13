package com.example.backend.model;

import com.example.backend.common.ObjectIdDeserializer;
import com.example.backend.enums.ResourceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "workflowInstances")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@ToString
public class WorkflowInstance {

    @Id
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String instanceId;
    private String workflowId;
    private String tenantId;
    private List<String> processId;
    private ResourceStatus status;
    private String currentStepId;
    @Builder.Default
    private InstanceMetadata metadata= new InstanceMetadata();
}