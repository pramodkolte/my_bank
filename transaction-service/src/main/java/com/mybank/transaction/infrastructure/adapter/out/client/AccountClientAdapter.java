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
        try {
            AccountDto account = client.getAccount(accountId);
            return account != null && "ACTIVE".equalsIgnoreCase(account.getStatus());
        } catch (Exception e) {
            return false;
        }
    }
}
