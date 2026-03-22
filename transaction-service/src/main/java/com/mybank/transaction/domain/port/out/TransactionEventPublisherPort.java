package com.mybank.transaction.domain.port.out;

import com.mybank.transaction.domain.model.Transaction;

public interface TransactionEventPublisherPort {
    void publishTransactionInitiatedEvent(Transaction transaction);
    void publishTransactionCompletedEvent(Transaction transaction);
    void publishTransactionFailedEvent(Transaction transaction);
}
