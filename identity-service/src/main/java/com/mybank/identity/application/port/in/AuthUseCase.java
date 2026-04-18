package com.mybank.identity.application.port.in;

import com.mybank.identity.domain.model.User;

public interface AuthUseCase {
    User register(String email, String rawPassword, String role);
    String login(String email, String rawPassword);
    void updateKycStatus(String email, String status);
    boolean verifyUser(java.util.UUID id);
}
