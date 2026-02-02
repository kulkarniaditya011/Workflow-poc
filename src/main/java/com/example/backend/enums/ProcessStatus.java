package com.example.backend.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProcessStatus {
    @JsonProperty(value = "DRAFT", access = JsonProperty.Access.READ_WRITE)
    DRAFT,

    @JsonProperty(value = "ACTIVE", access = JsonProperty.Access.READ_WRITE)
    ACTIVE,

    @JsonProperty(value = "DEPRECATED", access = JsonProperty.Access.READ_WRITE)
    DEPRECATED
}
