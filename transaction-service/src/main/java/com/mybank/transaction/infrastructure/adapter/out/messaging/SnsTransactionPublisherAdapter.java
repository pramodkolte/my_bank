package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnsTransactionPublisherAdapter implements TransactionEventPublisherPort {

    private final SnsTemplate snsTemplate;

    @SuppressWarnings("null")
    @Override
    @CircuitBreaker(name = "snsPublisher", fallbackMethod = "publishFallback")
    public void publishTransactionInitiatedEvent(Transaction transaction) {
        log.info("Sending TransactionInitiatedEvent to SNS topic: transaction-events-topic for ID: {}", transaction.getId());
        Map<String, Object> event = Map.of(
                "transactionId", transaction.getId(),
                "senderAccountId", transaction.getSenderAccountId(),
                "receiverAccountId", transaction.getReceiverAccountId(),
                "amount", transaction.getAmount());
        snsTemplate.convertAndSend("transaction-events-topic", event);
        log.info("Successfully published TransactionInitiatedEvent to SNS.");
    }

    @SuppressWarnings("null")
    @Override
    @CircuitBreaker(name = "snsPublisher", fallbackMethod = "publishFallback")
    public void publishTransactionCompletedEvent(Transaction transaction) {
        log.info("Sending TransactionCompletedEvent to SNS topic: banking-transactions-completed-topic for ID: {}", transaction.getId());
        snsTemplate.convertAndSend("banking-transactions-completed-topic", transaction);
        log.info("Successfully published TransactionCompletedEvent to SNS.");
    }

    @SuppressWarnings("null")
    @Override
    @CircuitBreaker(name = "snsPublisher", fallbackMethod = "publishFallback")
    public void publishTransactionFailedEvent(Transaction transaction) {
        log.info("Sending TransactionFailedEvent to SNS topic: banking-transactions-failed-topic for ID: {}", transaction.getId());
        snsTemplate.convertAndSend("banking-transactions-failed-topic", transaction);
        log.info("Successfully published TransactionFailedEvent to SNS.");
    }

    public void publishFallback(Transaction transaction, Throwable t) {
        log.error("SNS publishing failed for transaction {}. Circuit breaker fallback triggered.", transaction.getId(), t);
        throw new IllegalStateException("SNS is unreachable. Cannot process transaction.", t);
    }
}

