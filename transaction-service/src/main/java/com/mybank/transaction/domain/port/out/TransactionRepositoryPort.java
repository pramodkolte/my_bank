package com.mybank.transaction.domain.port.out;

import com.mybank.transaction.domain.model.Transaction;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
}
