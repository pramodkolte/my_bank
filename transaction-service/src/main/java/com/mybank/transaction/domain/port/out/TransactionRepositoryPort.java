package com.mybank.transaction.domain.port.out;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    List<Transaction> findTransactions(UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, TransactionStatus status, LocalDateTime startDate, LocalDateTime endDate);
}
