package com.mybank.transaction.application.port.in;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionUseCase {
    Transaction initiateTransfer(UUID authenticatedUserId, UUID senderId, UUID receiverId, BigDecimal amount);
    void completeTransaction(UUID transactionId);
    void failTransaction(UUID transactionId, String reason);
    List<Transaction> getTransactionHistory(UUID authenticatedUserId, UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> getAllTransactions(UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, TransactionStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
