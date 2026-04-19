package com.mybank.transaction.infrastructure.adapter.out.client;

import com.mybank.transaction.domain.port.out.AccountClientPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountClientAdapter implements AccountClientPort {

    private final AccountServiceClient client;

    @Override
    public boolean isAccountActive(UUID accountId) {
        return getAccount(accountId)
                .map(account -> "ACTIVE".equalsIgnoreCase(account.getStatus()))
                .orElse(false);
    }

    @Override
    public java.util.Optional<AccountDto> getAccount(UUID accountId) {
        try {
            return java.util.Optional.ofNullable(client.getAccount(accountId));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }
}
