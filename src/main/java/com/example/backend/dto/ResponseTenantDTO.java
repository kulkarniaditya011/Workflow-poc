package com.example.backend.dto;

import com.example.backend.model.ContactInfo;
import com.example.backend.model.TenantConfig;
import com.example.backend.model.TenantMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTenantDTO {
    private String tenantId; 

    private String name; 
    private String domain;
    private String status; 

    private TenantConfig config;
    
    private ContactInfo contactInfo;

    private TenantMetadata metadata;
}
