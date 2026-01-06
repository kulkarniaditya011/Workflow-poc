package com.example.backend.repository;

import com.example.backend.model.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends MongoRepository<Form, String> {
    @Query("{ 'tenantId': ?0, 'formId': ?1 }")
    Optional<Form> findByTenantAndFormId(String tenantId, String formId);

    @Query("{ 'tenantId': ?0 }")
    List<Form> findByTenant(String tenantId);

    long countByTenantId(String tenantId);

    Optional<Form> findByFormId(String formId);
}
