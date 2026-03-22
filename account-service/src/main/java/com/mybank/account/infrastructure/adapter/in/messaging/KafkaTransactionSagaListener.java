package com.mybank.account.infrastructure.adapter.in.messaging;

import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.infrastructure.adapter.in.messaging.dto.TransactionInitiatedEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTransactionSagaListener {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTransactionSagaListener.class);
    private final AccountUseCase accountUseCase;

    @KafkaListener(topics = "banking.transactions", groupId = "account-service-group")
    public void handleTransactionInitiated(TransactionInitiatedEvent event) {
        logger.info("Received TransactionInitiatedEvent for Transaction ID: {}", event.getTransactionId());
        accountUseCase.processTransfer(
                event.getTransactionId(),
                event.getSenderAccountId(),
                event.getReceiverAccountId(),
                event.getAmount()
        );
    }
}
