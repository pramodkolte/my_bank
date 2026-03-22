package com.mybank.identity.infrastructure.adapter.out.messaging;

import com.mybank.identity.domain.model.User;
import com.mybank.identity.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @SuppressWarnings("null")
    @Override
    public void publishUserRegisteredEvent(User user) {
        kafkaTemplate.send("user-registered-topic", user.getId().toString(), user);
    }

    @SuppressWarnings("null")
    @Override
    public void publishKycStatusUpdatedEvent(User user) {
        kafkaTemplate.send("kyc-status-updated-topic", user.getId().toString(), user);
    }
}
