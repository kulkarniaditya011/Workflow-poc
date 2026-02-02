package com.example.backend.repository;

import com.example.backend.model.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {

    Optional<Tenant> findByTenantId(String tenantId);

    @Query("{ 'status': ?0 }")
    List<Tenant> findByStatus(String status);

    @Query("{ 'domain': ?0 }")
    Optional<Tenant> findByDomain(String domain);

    boolean existsByTenantId(String tenantId);

    @Query("{ 'contactInfo.primaryContactEmail': ?0 }")
    Optional<Tenant> findByPrimaryContactEmail(String email);
}
