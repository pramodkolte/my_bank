package com.mybank.audit.infrastructure.adapter.in.messaging;

import com.mybank.audit.application.port.in.AuditUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsAuditListener {

    private final AuditUseCase auditUseCase;

    @SqsListener("audit-service-queue")
    public void handleWildcardEvents(String payload, @Header(value = "LogicalResourceId", required = false) String logicalResourceId) {
        // Fallback to "unknown-topic" if the topic isn't available
        String topic = logicalResourceId != null ? logicalResourceId : "unknown-topic";
        log.info("Intercepted event from SQS mapping to topic: {}", topic);
        auditUseCase.logEvent(topic, payload);
    }
}
