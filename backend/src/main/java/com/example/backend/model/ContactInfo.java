package com.example.backend.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ContactInfo {
    private String primaryContactName;
    private String primaryContactEmail;
    private String primaryContactPhone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
