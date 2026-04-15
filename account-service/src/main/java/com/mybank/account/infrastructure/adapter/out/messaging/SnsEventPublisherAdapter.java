package com.mybank.account.infrastructure.adapter.out.messaging;

import com.mybank.account.domain.model.Account;
import com.mybank.account.domain.port.out.AccountEventPublisherPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SnsEventPublisherAdapter implements AccountEventPublisherPort {

    private final SnsTemplate snsTemplate;

    @SuppressWarnings("null")
    @Override
    public void publishAccountCreatedEvent(Account account) {
        snsTemplate.convertAndSend("banking-accounts-topic", account);
    }

    @SuppressWarnings("null")
    @Override
    public void publishTransactionSuccessEvent(UUID transactionId) {
        snsTemplate.convertAndSend("banking-transactions-replies-topic", 
                Map.of("transactionId", transactionId, "status", "SUCCESS"));
    }

    @SuppressWarnings("null")
    @Override
    public void publishTransactionFailedEvent(UUID transactionId, String reason) {
        snsTemplate.convertAndSend("banking-transactions-replies-topic", 
                Map.of("transactionId", transactionId, "status", "FAILED", "reason", reason));
    }
}
