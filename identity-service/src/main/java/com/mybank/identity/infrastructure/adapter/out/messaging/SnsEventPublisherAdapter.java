package com.mybank.identity.infrastructure.adapter.out.messaging;

import com.mybank.identity.domain.model.User;
import com.mybank.identity.domain.port.out.EventPublisherPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnsEventPublisherAdapter implements EventPublisherPort {

    private final SnsTemplate snsTemplate;

    @SuppressWarnings("null")
    @Override
    public void publishUserRegisteredEvent(User user) {
        snsTemplate.convertAndSend("user-registered-topic", user);
    }

    @SuppressWarnings("null")
    @Override
    public void publishKycStatusUpdatedEvent(User user) {
        snsTemplate.convertAndSend("kyc-status-updated-topic", user);
    }
}
