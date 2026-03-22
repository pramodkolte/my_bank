package com.mybank.transaction.application.port.in;

import com.mybank.transaction.domain.model.Transaction;
import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionUseCase {
    Transaction initiateTransfer(UUID senderId, UUID receiverId, BigDecimal amount);
    void completeTransaction(UUID transactionId);
    void failTransaction(UUID transactionId, String reason);
}
