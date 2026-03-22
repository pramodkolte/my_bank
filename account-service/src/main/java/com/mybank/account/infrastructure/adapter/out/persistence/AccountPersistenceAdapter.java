package com.mybank.account.infrastructure.adapter.out.persistence;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.port.out.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final SpringDataAccountRepository repository;

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

    @Override
    public Optional<Account> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Account> findLockedById(UUID id) {
        return repository.findLockedById(id).map(this::mapToDomain);
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
