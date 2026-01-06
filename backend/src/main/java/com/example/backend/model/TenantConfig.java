package com.example.backend.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantConfig {
    private String timezone; // "Asia/Kolkata", "America/New_York"
    private String currency; // "INR", "USD"
    private String dateFormat; // "DD-MM-YYYY", "MM/DD/YYYY"
    private Map<String, Object> customSettings; // Tenant-specific settings
}
