package com.mybank.transaction.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private UUID id;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
}
