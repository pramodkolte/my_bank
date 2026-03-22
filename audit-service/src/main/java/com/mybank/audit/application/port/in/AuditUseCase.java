package com.mybank.audit.application.port.in;

public interface AuditUseCase {
    void logEvent(String topic, String payload);
}
