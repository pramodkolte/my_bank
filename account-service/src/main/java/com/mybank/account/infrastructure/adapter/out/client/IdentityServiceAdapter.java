package com.mybank.account.infrastructure.adapter.out.client;

import com.mybank.account.domain.port.out.IdentityProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityServiceAdapter implements IdentityProviderPort {

    private final IdentityServiceClient identityServiceClient;

    @Override
    public boolean verifyUserExists(UUID ownerId) {
        Boolean exists = identityServiceClient.verifyUserExists(ownerId);
        return Boolean.TRUE.equals(exists);
    }
}
