package com.example.backend.model;

import com.example.backend.common.ObjectIdDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndex(
        name = "unique_userId_per_tenant",
        def = "{'tenantId': 1, 'userId': 1}",
        unique = true
)
public class Users{
    @Id
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String userId;
    private String name;
    private String email;
    private String password;
    private List<String> roles;
    private List<String> departmentId;
    private String tenantId;

}
