package com.mybank.audit.application.service;

import com.mybank.audit.application.port.in.AuditUseCase;
import com.mybank.audit.domain.model.AuditRecord;
import com.mybank.audit.domain.port.out.AuditStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService implements AuditUseCase {

    private final AuditStoragePort auditStoragePort;

    @Override
    public void logEvent(String topic, String payload) {
        AuditRecord record = AuditRecord.builder()
                .id(UUID.randomUUID().toString())
                .topic(topic)
                .eventType(determineEventType(topic))
                .payload(payload)
                .timestamp(Instant.now())
                .build();
        
        auditStoragePort.save(record);
        log.info("Saved audit record for topic: {}", topic);
    }

    private String determineEventType(String topic) {
        if (topic.contains("transactions")) return "TRANSACTION";
        if (topic.contains("accounts")) return "ACCOUNT";
        if (topic.contains("user")) return "USER";
        return "UNKNOWN";
    }
}
