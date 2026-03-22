package com.mybank.account.application.port.in;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountUseCase {
    Account getAccount(UUID accountId);
    Account createAccount(UUID ownerId, Currency currency);
    Account updateBalance(UUID accountId, BigDecimal amount);
    void processTransfer(UUID transactionId, UUID senderId, UUID receiverId, BigDecimal amount);
}
