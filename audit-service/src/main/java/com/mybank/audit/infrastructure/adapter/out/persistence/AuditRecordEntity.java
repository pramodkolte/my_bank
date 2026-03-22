package com.mybank.audit.infrastructure.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_records")
public class AuditRecordEntity {
    @Id
    private String id;
    private String eventType;
    private String topic;
    private String payload;
    private Instant timestamp;
}
