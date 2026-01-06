package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "entities")
@Builder
public class Entities {
    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String entityName;

    @Indexed
    private String entityId;

    private Map<String, Property> properties;

    private String status;

    @Builder.Default
    private Metadata metadata=new Metadata();

}
