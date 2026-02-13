package com.example.backend.repository;

import com.example.backend.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {

    @Query("{ 'email' : ?0 }")
    Optional<Users> findByTenantIdAndEmail(String email);

    Optional<Users> findByTenantIdAndUserId(String tenantId, String userId);

    @Query("{ 'email' : ?0 }")
    Optional<Users> findByEmail(String email);

    @Query("{ 'userId' : ?0 }")
    Optional<Users> findByUserId(String userId);

    @Query(value = "{ 'roles': ?0 }", exists = true)
    boolean existsByRole(String role);

    Page<Users> findByTenantIdAndDepartmentId(String tenantId, String departmentId, Pageable pageable);
}
