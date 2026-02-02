package com.example.backend.repository;

import com.example.backend.model.Roles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends MongoRepository<Roles, String> {

    @Query("{ 'name' : { $in: ?0 } }")
    List<Roles> findByNameIn(List<String> names);
}
