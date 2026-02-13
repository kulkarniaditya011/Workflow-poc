    package com.example.backend.enums;

    import com.fasterxml.jackson.annotation.JsonProperty;

    public enum ProcessExecutionPattern {
        @JsonProperty(value = "SEQUENTIAL", access = JsonProperty.Access.READ_WRITE)
        SEQUENTIAL,

        @JsonProperty(value = "PARALLEL", access = JsonProperty.Access.READ_WRITE)
        PARALLEL
    }
