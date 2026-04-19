package com.mybank.transaction.infrastructure.adapter.out.client;

import com.mybank.transaction.domain.port.out.AccountClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
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
            // Try fetching full account details first (includes balance)
            var response = client.getAccount(accountId);
            return java.util.Optional.ofNullable(response.getData());
        } catch (feign.FeignException.Forbidden e) {
            log.info("Access denied for full account details of {}. Falling back to status check.", accountId);
            try {
                // Fallback to minimal status check if full details are restricted (e.g. for receiver account)
                var statusResponse = client.getAccountStatus(accountId);
                AccountStatusDto statusData = statusResponse.getData();
                if (statusData != null) {
                    AccountDto dto = new AccountDto();
                    dto.setId(statusData.getId());
                    dto.setOwnerId(statusData.getOwnerId());
                    dto.setStatus(statusData.getStatus());
                    dto.setBalance(java.math.BigDecimal.ZERO); // Balance not available in status check
                    return java.util.Optional.of(dto);
                }
            } catch (Exception ex) {
                log.error("Failed to fetch account status for {}: {}", accountId, ex.getMessage());
            }
            return java.util.Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch account {} from account-service: {}", accountId, e.getMessage());
            return java.util.Optional.empty();
        }
    }
}
