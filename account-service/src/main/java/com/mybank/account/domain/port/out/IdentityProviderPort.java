package com.mybank.account.domain.port.out;

import java.util.UUID;

public interface IdentityProviderPort {
    boolean verifyUserExists(UUID ownerId);
}
