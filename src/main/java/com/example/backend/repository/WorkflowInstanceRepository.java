package com.example.backend.repository;

import com.example.backend.enums.ResourceStatus;
import com.example.backend.model.WorkflowInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<WorkflowInstance> findByTenantIdAndStatus(String tenantId, ResourceStatus status, Pageable pageable);

    @Query("{ 'tenantId': ?0 }")
    List<WorkflowInstance> findByTenant(String tenantId);

}
