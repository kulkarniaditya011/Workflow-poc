package com.example.backend.repository;

import com.example.backend.model.Entities;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntityRepository extends MongoRepository<Entities, String> {
    @Query("{ 'tenantId': ?0, 'entityId': ?1 }")
    Optional<Entities> findByTenantAndEntityId(String tenantId, String entityId);

    @Query("{ 'tenantId': ?0, 'entityType': ?1 }")
    List<Entities> findByTenantAndEntityType(String tenantId, String entityType);

    @Query("{ 'tenantId': ?0 }")
    List<Entities> findByTenant(String tenantId);

    long countByTenantId(String tenantId);
}
