package com.example.backend.repository;

import com.example.backend.model.Departments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentsRepository extends MongoRepository<Departments, String> {

    Optional<Departments> findByTenantIdAndDepartmentId(String tenantId, String departmentId);

    Page<Departments> findByTenantId(String tenantId, Pageable pageable);

}
