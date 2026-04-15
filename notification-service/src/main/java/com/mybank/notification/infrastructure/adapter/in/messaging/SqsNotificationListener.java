package com.mybank.notification.infrastructure.adapter.in.messaging;

import com.mybank.notification.application.port.in.NotificationUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsNotificationListener {

    private final NotificationUseCase notificationUseCase;

    @SqsListener("notification-service-queue")
    public void handleTransactionCompleted(Map<String, Object> payload) {
        log.info("Received transaction completed event from SQS. Spawning virtual thread for notification.");
        notificationUseCase.processTransactionCompleted(payload);
    }
}
