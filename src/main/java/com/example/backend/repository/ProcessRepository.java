package com.example.backend.repository;

import com.example.backend.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends MongoRepository<Process, String> {
    Page<Process> findByTenantIdAndProcessIdIn(String tenantId, List<String> processId, Pageable pageable);
    Page<Process> findByTenantIdAndDepartmentId(String tenantId, String departmentId, Pageable pageable);


    Optional<Process> findByTenantIdAndProcessId(String tenantId, String processId);


    Page<Process> findByTenantId(String tenantId, Pageable pageable);
    Optional<Process> findByTenantId(String tenantId);

}
