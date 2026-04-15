package com.mybank.account.infrastructure.adapter.in.messaging;

import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.infrastructure.adapter.in.messaging.dto.TransactionInitiatedEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsTransactionSagaListener {

    private static final Logger logger = LoggerFactory.getLogger(SqsTransactionSagaListener.class);
    private final AccountUseCase accountUseCase;

    @SqsListener("account-service-queue")
    public void handleTransactionInitiated(TransactionInitiatedEvent event) {
        logger.info("Received TransactionInitiatedEvent from SQS for Transaction ID: {}", event.getTransactionId());
        accountUseCase.processTransfer(
                event.getTransactionId(),
                event.getSenderAccountId(),
                event.getReceiverAccountId(),
                event.getAmount()
        );
    }
}
