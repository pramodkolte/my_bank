package com.mybank.account.domain.port.out;

import com.mybank.account.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findLockedById(UUID id); // For pessimistic lock
}
