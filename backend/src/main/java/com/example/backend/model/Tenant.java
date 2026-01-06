package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "tenants")
public class Tenant {
    @Id
    @JsonProperty("_id")
    private String id;

    @Indexed(unique = true)
    private String tenantId; // Unique identifier: "bank-xyz", "company-abc"

    private String name; // Display name: "XYZ Bank", "ABC Corporation"
    private String domain; // Optional: "xyzbank.com"
    private String status; // active, suspended, inactive

    // Tenant configuration
    private TenantConfig config;

    // Contact information
    private ContactInfo contactInfo;

    @Builder.Default
    private TenantMetadata metadata = new TenantMetadata();
}
