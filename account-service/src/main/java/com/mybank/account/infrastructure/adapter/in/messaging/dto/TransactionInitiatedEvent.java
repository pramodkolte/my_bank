package com.mybank.account.infrastructure.adapter.in.messaging.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionInitiatedEvent {
    private UUID transactionId;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
}
