package com.example.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoleDTO {
    private String id;

    @NotEmpty(message = "Role should have a name")
    private String name;

    @NotEmpty(message = "Authorities cannot be empty")
    private List<String> authorities;
}
