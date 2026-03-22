package com.mybank.account.infrastructure.adapter.out.messaging;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.port.out.AccountEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements AccountEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishAccountCreatedEvent(Account account) {
        kafkaTemplate.send("banking.accounts", account.getId().toString(), account);
    }

    @Override
    public void publishTransactionSuccessEvent(UUID transactionId) {
        kafkaTemplate.send("banking.transactions.replies", transactionId.toString(), 
                Map.of("transactionId", transactionId, "status", "SUCCESS"));
    }

    @Override
    public void publishTransactionFailedEvent(UUID transactionId, String reason) {
        kafkaTemplate.send("banking.transactions.replies", transactionId.toString(), 
                Map.of("transactionId", transactionId, "status", "FAILED", "reason", reason));
    }
}
