package com.mybank.notification.infrastructure.adapter.in.messaging;

import com.mybank.notification.application.port.in.NotificationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationListener {

    private final NotificationUseCase notificationUseCase;

    @KafkaListener(topics = "banking.transactions.completed", groupId = "notification-service-group")
    public void handleTransactionCompleted(Map<String, Object> payload) {
        log.info("Received transaction completed event. Spawning virtual thread for notification.");
        notificationUseCase.processTransactionCompleted(payload);
    }
}
