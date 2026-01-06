package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubscriptionInfo {
    private String plan; // free, basic, premium, enterprise
    private Instant startDate;
    private Instant endDate;
    private Boolean isActive;
    private Map<String, Object> planDetails;
}
