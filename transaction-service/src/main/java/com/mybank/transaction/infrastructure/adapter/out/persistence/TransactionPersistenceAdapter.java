package com.mybank.transaction.infrastructure.adapter.out.persistence;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {

    private final SpringDataTransactionRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.builder()
                .id(transaction.getId())
                .senderAccountId(transaction.getSenderAccountId())
                .receiverAccountId(transaction.getReceiverAccountId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .failureReason(transaction.getFailureReason())
                .build();
        @SuppressWarnings("null")
        TransactionEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Transaction> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private Transaction mapToDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .senderAccountId(entity.getSenderAccountId())
                .receiverAccountId(entity.getReceiverAccountId())
                .amount(entity.getAmount())
                .type(entity.getType())
                .status(entity.getStatus())
                .failureReason(entity.getFailureReason())
                .build();
    }
}
