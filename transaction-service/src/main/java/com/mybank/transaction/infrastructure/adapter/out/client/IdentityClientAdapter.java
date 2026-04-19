package com.mybank.transaction.infrastructure.adapter.out.client;

import com.mybank.transaction.domain.port.out.IdentityClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdentityClientAdapter implements IdentityClientPort {

    private final IdentityServiceClient identityServiceClient;

    @Override
    public boolean isUserVerified(UUID userId) {
        try {
            var response = identityServiceClient.verifyUser(userId);
            return response != null && Boolean.TRUE.equals(response.getData());
        } catch (Exception e) {
            log.error("Failed to verify user {} with identity-service: {}", userId, e.getMessage());
            return false;
        }
    }
}
