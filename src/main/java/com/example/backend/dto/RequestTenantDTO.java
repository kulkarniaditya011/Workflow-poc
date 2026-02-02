package com.example.backend.dto;

import com.example.backend.model.ContactInfo;
import com.example.backend.model.TenantConfig;
import com.example.backend.model.TenantMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestTenantDTO {

    private String id;

    @NotNull(message = "Tenant id cannot be empty")
    private String tenantId; // Unique identifier: "bank-xyz", "company-abc"

    private String name; // Display name: "XYZ Bank", "ABC Corporation"
    private String domain; // Optional: "xyzbank.com"
    private String status; // active, suspended, inactive

    // Tenant configuration
    private TenantConfig config;

    // Contact information
    private ContactInfo contactInfo;

    private TenantMetadata metadata;
}

