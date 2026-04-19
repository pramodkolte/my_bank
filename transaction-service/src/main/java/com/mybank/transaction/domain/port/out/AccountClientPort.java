package com.mybank.transaction.domain.port.out;

import com.mybank.transaction.infrastructure.adapter.out.client.AccountDto;

import java.util.Optional;
import java.util.UUID;

public interface AccountClientPort {
    boolean isAccountActive(UUID accountId);
    Optional<AccountDto> getAccount(UUID accountId);
}
