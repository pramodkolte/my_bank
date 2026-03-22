package com.mybank.account.domain.port.out;

import com.mybank.account.domain.model.Account;
import java.util.UUID;

public interface AccountEventPublisherPort {
    void publishAccountCreatedEvent(Account account);
    void publishTransactionSuccessEvent(UUID transactionId);
    void publishTransactionFailedEvent(UUID transactionId, String reason);
}
