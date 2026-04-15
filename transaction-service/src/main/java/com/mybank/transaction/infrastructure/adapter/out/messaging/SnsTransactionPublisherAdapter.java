package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SnsTransactionPublisherAdapter implements TransactionEventPublisherPort {

    private final SnsTemplate snsTemplate;

    @Override
    @CircuitBreaker(name = "snsPublisher", fallbackMethod = "publishFallback")
    public void publishTransactionInitiatedEvent(Transaction transaction) {
        Map<String, Object> event = Map.of(
            "transactionId", transaction.getId(),
            "senderAccountId", transaction.getSenderAccountId(),
            "receiverAccountId", transaction.getReceiverAccountId(),
            "amount", transaction.getAmount()
        );
        snsTemplate.convertAndSend("transaction-events-topic", event);
    }

    @Override
    public void publishTransactionCompletedEvent(Transaction transaction) {
        snsTemplate.convertAndSend("banking-transactions-completed-topic", transaction);
    }

    @Override
    public void publishTransactionFailedEvent(Transaction transaction) {
        snsTemplate.convertAndSend("banking-transactions-failed-topic", transaction);
    }

    public void publishFallback(Transaction transaction, Throwable t) {
        throw new IllegalStateException("SNS is unreachable. Cannot process transaction.", t);
    }
}
