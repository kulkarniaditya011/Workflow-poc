package com.example.backend.repository;

import com.example.backend.model.WorkflowInstance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowInstanceRepository extends MongoRepository<WorkflowInstance, String> {
    @Query("{ 'tenantId': ?0, 'instanceId': ?1 }")
    Optional<WorkflowInstance> findByTenantAndInstanceId(String tenantId, String instanceId);

    @Query("{ 'tenantId': ?0, 'workflowId': ?1 }")
    List<WorkflowInstance> findByTenantAndWorkflowId(String tenantId, String workflowId);

    @Query("{ 'tenantId': ?0, 'status': ?1 }")
    List<WorkflowInstance> findByTenantAndStatus(String tenantId, String status);

    @Query("{ 'tenantId': ?0 }")
    List<WorkflowInstance> findByTenant(String tenantId);
}
