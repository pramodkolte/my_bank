package com.mybank.identity.domain.port.out;

import com.mybank.identity.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(java.util.UUID id);
    boolean existsByEmail(String email);
}
