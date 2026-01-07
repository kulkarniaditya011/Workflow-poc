package com.example.backend.repository;

import com.example.backend.model.Forms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends MongoRepository<Forms, String> {
    @Query("{ 'tenantId': ?0, 'formId': ?1 }")
    Optional<Forms> findByTenantIdAndFormId(String tenantId, String formId);

    @Query("{ 'tenantId': ?0 }")
    List<Forms> findByTenant(String tenantId);

    long countByTenantId(String tenantId);

    @Query("{ 'formId': ?0 }")
    List<Forms> findByFormId(String formId);
}
