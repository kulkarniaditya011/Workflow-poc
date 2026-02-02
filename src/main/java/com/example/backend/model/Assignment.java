package com.example.backend.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assignment {
    private List<String> roles;
    private List<String> users;
}
