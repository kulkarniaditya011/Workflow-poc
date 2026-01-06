package com.example.backend.repository;

import com.example.backend.model.Process;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProcessRepository extends MongoRepository<Process, String> {

    Optional<Process> findByProcessId(String processId);

}
