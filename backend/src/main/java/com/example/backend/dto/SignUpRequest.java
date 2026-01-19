package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {

    private String name;

    @NotEmpty(message = "email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotEmpty(message = "password is required")
    private String password;

}
