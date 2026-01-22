package com.example.backend.repository;

import com.example.backend.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<Users, String> {

    @Query("{ 'email' : ?0 }")
    Optional<Users> findByEmail(String email);
}
