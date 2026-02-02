package com.example.backend.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StepType {
    @JsonProperty(value = "APPROVAL", access = JsonProperty.Access.READ_WRITE)
    APPROVAL,

    @JsonProperty(value = "VERIFICATION", access = JsonProperty.Access.READ_WRITE)
    VERIFICATION,

    @JsonProperty(value = "FORM_SUBMISSION", access = JsonProperty.Access.READ_WRITE)
    FORM_SUBMISSION,

    @JsonProperty(value = "NOTIFICATION", access = JsonProperty.Access.READ_WRITE)
    NOTIFICATION,

    @JsonProperty(value = "SYSTEM_TASK", access = JsonProperty.Access.READ_WRITE)
    SYSTEM_TASK
}
