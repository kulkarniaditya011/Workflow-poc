package com.example.backend.repository;

import com.example.backend.model.Workflow;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends MongoRepository<Workflow, String> {
    @Query("{ 'tenantId': ?0, 'workflowId': ?1 }")
    Optional<Workflow> findByTenantIdAndWorkflowId(String tenantId, String workflowId);

    Page<Workflow> findByTenantIdAndDepartmentId(String tenantId, String departmentId, Pageable pageable);

    Page<Workflow> findByTenantId(String tenantId, Pageable pageable);

    List<Workflow> findByTenantIdAndWorkflowIdIn(String tenantId, List<String> workflowId);

}
