package com.mybank.account.infrastructure.adapter.out.persistence;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.model.AccountStatus;
import com.mybank.account.domain.port.out.AccountRepositoryPort;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository repository;

    @SuppressWarnings("null")
    @Override
    public Account save(Account account) {
        AccountEntity entity = AccountEntity.builder()
                .id(account.getId())
                .ownerId(account.getOwnerId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .build();
        AccountEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @SuppressWarnings("null")
    @Override
    public Optional<Account> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Account> findLockedById(UUID id) {
        return repository.findLockedById(id).map(this::mapToDomain);
    }

    @Override
    public List<Account> findByOwnerId(UUID ownerId) {
        return repository.findByOwnerId(ownerId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findAll(UUID ownerId, String currency, String status, BigDecimal minBalance, BigDecimal maxBalance) {
        Specification<AccountEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (ownerId != null) {
                predicates.add(cb.equal(root.get("ownerId"), ownerId));
            }
            if (currency != null && !currency.isEmpty()) {
                predicates.add(cb.equal(root.get("currency"), currency));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), AccountStatus.valueOf(status.toUpperCase())));
            }
            if (minBalance != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("balance"), minBalance));
            }
            if (maxBalance != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("balance"), maxBalance));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Account mapToDomain(AccountEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .balance(entity.getBalance())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .build();
    }
}
