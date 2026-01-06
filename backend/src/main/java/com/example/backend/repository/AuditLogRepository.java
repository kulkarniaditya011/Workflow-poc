package com.example.backend.repository;

import com.example.backend.model.AuditLogs;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogs, String> {
    @Query("{ 'tenantId': ?0 }")
    List<AuditLogs> findByTenant(String tenantId);

    @Query("{ 'tenantId': ?0, 'action': ?1 }")
    List<AuditLogs> findByTenantAndAction(String tenantId, String action);

    @Query("{ 'tenantId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<AuditLogs> findByTenantAndTimestampBetween(String tenantId, Instant start, Instant end);
}
