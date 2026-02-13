package com.example.backend.repository;

import com.example.backend.model.Forms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormRepository extends MongoRepository<Forms, String> {

    Page<Forms> findByTenantIdAndDepartmentId(String tenantId, String departmentId, Pageable pageable);
    Page<Forms> findByTenantId(String tenantId, Pageable pageable);
    Optional<Forms> findByTenantIdAndFormId(String tenantId, String formId);
}
