package com.example.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Property {
    private Object value; // Actual data
    private String type; // string, number, boolean, date, array, object
    private boolean indexed; // Should this property be searchable?
    private boolean required; // Is this mandatory?
    private String validationRule; // Optional validate (e.g., "email", "phone")
}
