package com.example.backend.model;

import com.example.backend.common.ObjectIdDeserializer;
import com.example.backend.enums.ResourceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(collection = "workflows")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workflow {
    @Id
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @Indexed
    private String tenantId;

    private String workflowId;

    private String name;
    private String description;
    private String departmentId;
    private ResourceStatus status;
    private String version;

    private List<String> processId;

    private ApprovalMetadata approval;

    @Builder.Default
    private WorkflowMetadata metadata= new WorkflowMetadata();

}
