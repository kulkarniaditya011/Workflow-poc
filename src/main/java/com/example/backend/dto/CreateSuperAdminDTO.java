package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateSuperAdminDTO {
    private String name;

    @NotEmpty(message = "email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotEmpty(message = "password is required")
    private String password;
    private List<String> roles;
}
