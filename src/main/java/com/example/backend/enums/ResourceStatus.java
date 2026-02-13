package com.example.backend.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ResourceStatus {

    @JsonProperty(value = "ACTIVE", access = JsonProperty.Access.READ_WRITE)
    ACTIVE,

    @JsonProperty(value = "CLOSED", access = JsonProperty.Access.READ_WRITE)
    CLOSED,

    @JsonProperty(value = "APPROVED", access = JsonProperty.Access.READ_WRITE)
    APPROVED,

    @JsonProperty(value = "REJECTED", access = JsonProperty.Access.READ_WRITE)
    REJECTED,

    @JsonProperty(value = "PENDING", access = JsonProperty.Access.READ_WRITE)
    PENDING,

    @JsonProperty(value = "IN_PROGRESS", access = JsonProperty.Access.READ_WRITE)
    IN_PROGRESS,

    @JsonProperty(value = "COMPLETED", access = JsonProperty.Access.READ_WRITE)
    COMPLETED,

    @JsonProperty(value = "DRAFT", access = JsonProperty.Access.READ_WRITE)
    DRAFT
}
