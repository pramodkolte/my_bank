package com.mybank.transaction.infrastructure.adapter.in.messaging;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaSagaReplyListener {
    
    private final TransactionUseCase transactionUseCase;

    @KafkaListener(topics = "banking.transactions.replies", groupId = "transaction-service-group")
    public void handleSagaReply(Map<String, Object> payload) {
        UUID transactionId = UUID.fromString((String) payload.get("transactionId"));
        String status = (String) payload.get("status");
        
        if ("SUCCESS".equals(status)) {
            transactionUseCase.completeTransaction(transactionId);
        } else {
            String reason = (String) payload.get("reason");
            transactionUseCase.failTransaction(transactionId, reason);
        }
    }
}
