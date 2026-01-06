package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "forms")
public class Form {

    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed

    private String tenantId;

    @Indexed

    private String formId;

    private String name;
    private String description;


    private List<FormField> fields;

    private String status;


    @Builder.Default
    private FormMetadata metadata = new FormMetadata();
}
