package com.mybank.audit.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAuditRepository extends MongoRepository<AuditRecordEntity, String> {
}
