package com.mybank.identity.domain.port.out;

import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import com.mybank.identity.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    List<User> findAll(String email, Role role, KYCStatus kycStatus);
    void deleteById(UUID id);
}
