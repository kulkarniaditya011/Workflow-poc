package com.example.backend.repository;

import com.example.backend.model.Workflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends MongoRepository<Workflow, String> {
    @Query("{ 'tenantId': ?0, 'workflowId': ?1 }")
    Optional<Workflow> findByTentantAndWorkflowId(String tenantId, String workflowId);

    @Query("{ 'tenantId': ?0, 'status': ?1 }")
    List<Workflow> findByTentantAndStatus(String tenantId, String status);

    @Query("{ 'tenantId': ?0 }")
    List<Workflow> findByTentant(String tenantId);


    List<Workflow> findByTenantIdAndDepartmentId(String tenantId, String departmentId);

    boolean existsBytenantIdAndWorkflowId(String tenantId, String workflowId);
}
