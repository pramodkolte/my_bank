package com.mybank.transaction.infrastructure.adapter.out.messaging;

import com.mybank.transaction.domain.event.TransactionCompletedEvent;
import com.mybank.transaction.domain.event.TransactionFailedEvent;
import com.mybank.transaction.domain.event.TransactionInitiatedEvent;
import com.mybank.transaction.domain.port.out.TransactionEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionEventPublisherPort publisherPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionInitiated(TransactionInitiatedEvent event) {
        log.info("Transaction {} committed. Publishing INITIATED event to SNS.", event.getTransaction().getId());
        publisherPort.publishTransactionInitiatedEvent(event.getTransaction());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Transaction {} committed. Publishing COMPLETED event to SNS.", event.getTransaction().getId());
        publisherPort.publishTransactionCompletedEvent(event.getTransaction());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionFailed(TransactionFailedEvent event) {
        log.info("Transaction {} committed. Publishing FAILED event to SNS.", event.getTransaction().getId());
        publisherPort.publishTransactionFailedEvent(event.getTransaction());
    }
}
