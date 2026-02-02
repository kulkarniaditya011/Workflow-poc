package com.example.backend.repository;

import com.example.backend.model.Forms;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends MongoRepository<Forms, String> {

    List<Forms> findByTenantIdAndDepartmentId(String tenantId, String departmentId);
}
