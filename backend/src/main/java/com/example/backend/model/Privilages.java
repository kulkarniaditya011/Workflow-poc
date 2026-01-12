package com.example.backend.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "privilages")
public class Privilages {


    private String id;
    private String name;
}
