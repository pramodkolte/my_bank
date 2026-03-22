package com.mybank.audit.infrastructure.adapter.out.persistence;

import com.mybank.audit.domain.model.AuditRecord;
import com.mybank.audit.domain.port.out.AuditStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoAuditStorageAdapter implements AuditStoragePort {

    private final MongoAuditRepository repository;

    @Override
    public void save(AuditRecord record) {
        AuditRecordEntity entity = AuditRecordEntity.builder()
                .id(record.getId())
                .eventType(record.getEventType())
                .topic(record.getTopic())
                .payload(record.getPayload())
                .timestamp(record.getTimestamp())
                .build();
        repository.save(entity);
    }
}
