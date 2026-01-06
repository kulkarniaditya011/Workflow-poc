package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Document(collection = "workflows")
public class Workflow {
    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed
    private String tenantId;

    private String workflowId;

    private String name;
    private String description;
    private String status;
    private String version;

    private List<String> processId;

    @Builder.Default
    private WorkflowMetadata metadata= new WorkflowMetadata();

}
