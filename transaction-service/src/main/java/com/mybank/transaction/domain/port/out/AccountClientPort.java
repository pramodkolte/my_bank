package com.mybank.transaction.domain.port.out;

import java.util.UUID;

public interface AccountClientPort {
    boolean isAccountActive(UUID accountId);
}
