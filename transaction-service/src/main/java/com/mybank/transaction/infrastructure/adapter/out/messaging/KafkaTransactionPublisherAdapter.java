package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaTransactionPublisherAdapter implements TransactionEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @CircuitBreaker(name = "kafkaPublisher", fallbackMethod = "publishFallback")
    public void publishTransactionInitiatedEvent(Transaction transaction) {
        Map<String, Object> event = Map.of(
            "transactionId", transaction.getId(),
            "senderAccountId", transaction.getSenderAccountId(),
            "receiverAccountId", transaction.getReceiverAccountId(),
            "amount", transaction.getAmount()
        );
        kafkaTemplate.send("banking.transactions", transaction.getId().toString(), event);
    }

    @Override
    public void publishTransactionCompletedEvent(Transaction transaction) {
        kafkaTemplate.send("banking.transactions.completed", transaction.getId().toString(), transaction);
    }

    @Override
    public void publishTransactionFailedEvent(Transaction transaction) {
        kafkaTemplate.send("banking.transactions.failed", transaction.getId().toString(), transaction);
    }

    public void publishFallback(Transaction transaction, Throwable t) {
        throw new IllegalStateException("Kafka broker is unreachable. Cannot process transaction.", t);
    }
}
