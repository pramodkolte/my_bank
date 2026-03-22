package com.mybank.identity.application.port.out;

import com.mybank.identity.domain.model.User;

public interface JwtProviderPort {
    String generateToken(User user);
}
