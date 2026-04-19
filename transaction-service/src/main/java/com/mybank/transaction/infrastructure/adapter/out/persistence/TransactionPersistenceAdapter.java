package com.mybank.transaction.infrastructure.adapter.out.persistence;

import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import com.mybank.transaction.domain.port.out.TransactionRepositoryPort;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .createdAt(transaction.getCreatedAt())
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

    @Override
    public List<Transaction> findTransactions(UUID accountId, BigDecimal minAmount, BigDecimal maxAmount, TransactionType type, TransactionStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<TransactionEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (accountId != null) {
                // Account filter (Sender OR Receiver)
                predicates.add(cb.or(
                        cb.equal(root.get("senderAccountId"), accountId),
                        cb.equal(root.get("receiverAccountId"), accountId)
                ));
            }

            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
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
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
