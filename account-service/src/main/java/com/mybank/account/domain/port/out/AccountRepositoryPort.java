package com.mybank.account.domain.port.out;

import com.mybank.account.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findLockedById(UUID id); // For pessimistic lock
    List<Account> findByOwnerId(UUID ownerId);
    List<Account> findAll(UUID ownerId, String currency, String status, BigDecimal minBalance, BigDecimal maxBalance);
    void deleteById(UUID id);
}
