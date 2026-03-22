package com.mybank.audit.infrastructure.adapter.in.messaging;

import com.mybank.audit.application.port.in.AuditUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAuditListener {

    private final AuditUseCase auditUseCase;

    // Use regular expression to dynamically match multiple banking topics seamlessly
    @KafkaListener(topicPattern = "banking.*|user-registered-topic", groupId = "audit-service-group")
    public void handleWildcardEvents(ConsumerRecord<String, String> record) {
        log.info("Intercepted event from topic: {}", record.topic());
        auditUseCase.logEvent(record.topic(), record.value());
    }
}
