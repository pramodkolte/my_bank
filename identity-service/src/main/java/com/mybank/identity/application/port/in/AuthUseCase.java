package com.mybank.identity.application.port.in;

import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import com.mybank.identity.domain.model.User;
import java.util.List;
import java.util.UUID;

public interface AuthUseCase {
    User register(String email, String rawPassword, String role);
    String login(String email, String rawPassword);
    void updateKycStatus(String email, String status);
    void updateKycStatus(UUID userId, String status);
    boolean verifyUser(UUID id);
    List<User> getUsers(String email, Role role, KYCStatus kycStatus);
    User getUserById(UUID id);
    User updateUser(UUID id, String email, Role role, KYCStatus kycStatus);
    void deleteUser(UUID id);
}
