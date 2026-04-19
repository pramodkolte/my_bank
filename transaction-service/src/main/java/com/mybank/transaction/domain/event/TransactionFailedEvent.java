package com.mybank.transaction.domain.event;

import com.mybank.transaction.domain.model.Transaction;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TransactionFailedEvent extends ApplicationEvent {
    private final Transaction transaction;

    public TransactionFailedEvent(Object source, Transaction transaction) {
        super(source);
        this.transaction = transaction;
    }
}
