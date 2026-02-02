package com.example.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private String tenantId;
    private String userId;
    private String name;
    private String email;
    private List<String> roles;
    private List<String> departmentId;
}
