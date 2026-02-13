package com.example.backend.model;

import com.example.backend.common.InstantFromMongoDeserializer;
import com.example.backend.enums.ResourceStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ApprovalMetadata {
    private ResourceStatus status; //used for either approve or reject

    private String actionBy; //user id
    @JsonDeserialize(using = InstantFromMongoDeserializer.class)
    private Instant actionAt;
    private String comment;
}
