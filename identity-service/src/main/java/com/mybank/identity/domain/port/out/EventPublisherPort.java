package com.mybank.identity.domain.port.out;

import com.mybank.identity.domain.model.User;

public interface EventPublisherPort {
    void publishUserRegisteredEvent(User user);
    void publishKycStatusUpdatedEvent(User user);
}
