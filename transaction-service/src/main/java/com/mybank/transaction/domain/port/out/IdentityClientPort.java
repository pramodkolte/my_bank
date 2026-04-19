package com.mybank.transaction.domain.port.out;

import java.util.UUID;

public interface IdentityClientPort {
    boolean isUserVerified(UUID userId);
}
