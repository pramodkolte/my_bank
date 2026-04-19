package com.mybank.account.application.port.in;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.model.AccountStatus;
import com.mybank.account.domain.model.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountUseCase {
    Account getAccount(UUID accountId);
    Account createAccount(UUID ownerId, Currency currency);
    Account updateBalance(UUID accountId, BigDecimal amount);
    void processTransfer(UUID transactionId, UUID senderId, UUID receiverId, BigDecimal amount);
    List<Account> getAccountsByOwner(UUID ownerId);
    List<Account> getAccounts(UUID ownerId, String currency, String status, BigDecimal minBalance, BigDecimal maxBalance);
    Account updateAccount(UUID accountId, String status, BigDecimal balance);
    void deleteAccount(UUID accountId);
}
