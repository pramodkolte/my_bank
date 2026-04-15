package com.mybank.transaction.infrastructure.adapter.in.messaging;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SqsSagaReplyListener {
    
    private final TransactionUseCase transactionUseCase;

    @SqsListener("transaction-service-queue")
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
