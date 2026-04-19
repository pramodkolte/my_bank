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
            var response = client.getAccount(accountId);
            return java.util.Optional.ofNullable(response.getData());
        } catch (Exception e) {
            // Logs would be helpful here in a real app to see why the call failed
            return java.util.Optional.empty();
        }
    }
}
