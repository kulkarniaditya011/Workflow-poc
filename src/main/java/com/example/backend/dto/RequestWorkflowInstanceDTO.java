package com.example.backend.dto;

import com.example.backend.model.InstanceMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestWorkflowInstanceDTO {

    @NotNull(message = "Instance id cannot be blank")
    private String instanceId;
    private InstanceMetadata metadata;

}
