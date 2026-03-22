package com.mybank.notification.application.service;

import com.mybank.notification.application.port.in.NotificationUseCase;
import com.mybank.notification.domain.model.Notification;
import com.mybank.notification.domain.port.out.NotificationDispatcherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class NotificationService implements NotificationUseCase {

    private final NotificationDispatcherPort dispatcherPort;
    // Java 25 Virtual Threads for unbounded scalable concurrency
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public NotificationService(NotificationDispatcherPort dispatcherPort) {
        this.dispatcherPort = dispatcherPort;
    }

    @Override
    public void processTransactionCompleted(Map<String, Object> transactionPayload) {
        log.info("Processing transaction payload for notification: {}", transactionPayload);
        
        // Dispatch processing on a virtual thread to prevent blocking the Kafka Consumer Thread
        virtualThreadExecutor.submit(() -> {
            try {
                UUID transactionId = UUID.fromString(transactionPayload.get("id").toString());
                String senderId = transactionPayload.get("senderAccountId").toString();
                String receiverId = transactionPayload.get("receiverAccountId").toString();
                BigDecimal amount = new BigDecimal(transactionPayload.get("amount").toString());

                Notification senderNotification = Notification.builder()
                        .type("EMAIL")
                        .recipient(senderId + "@bank.internal")
                        .subject("Transfer Completed")
                        .message("You sent " + amount + " to " + receiverId)
                        .transactionId(transactionId)
                        .amount(amount)
                        .build();

                Notification receiverNotification = Notification.builder()
                        .type("EMAIL")
                        .recipient(receiverId + "@bank.internal")
                        .subject("Funds Received")
                        .message("You received " + amount + " from " + senderId)
                        .transactionId(transactionId)
                        .amount(amount)
                        .build();

                dispatcherPort.dispatch(senderNotification);
                dispatcherPort.dispatch(receiverNotification);

            } catch (Exception e) {
                log.error("Failed to process notification via Virtual Thread", e);
            }
        });
    }
}
