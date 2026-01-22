package com.example.backend.repository;

import com.example.backend.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    @Query("{ 'name' : { $in: ?0 } }")
    List<Role> findByNameIn(List<String> names);
}
