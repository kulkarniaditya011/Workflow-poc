package com.example.backend.repository;

import com.example.backend.model.Process;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends MongoRepository<Process, String> {

    Optional<Process> findByProcessId(String processId);
    List<Process> findByTenantIdAndDepartmentId(String tenantId, String departmentId);

}
