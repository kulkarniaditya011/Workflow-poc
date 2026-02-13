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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "forms")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Forms {

    @Id
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String tenantId;
    private String formId;
    private String name;
    private String description;
    private List<String> departmentId;
    private List<FormField> fields;
    private ResourceStatus status;
    private ApprovalMetadata approval;
    private FormMetadata metadata;
}
