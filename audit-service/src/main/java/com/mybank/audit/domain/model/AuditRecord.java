package com.mybank.audit.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class AuditRecord {
    private String id; 
    private String eventType;
    private String topic;
    private String payload;
    private Instant timestamp;
}
