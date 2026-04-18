package com.mybank.identity.infrastructure.adapter.out.persistence;

import com.mybank.identity.domain.model.User;
import com.mybank.identity.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    @SuppressWarnings("null")
    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .kycStatus(user.getKycStatus())
                .build();
        UserEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findById(java.util.UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private User mapToDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .role(entity.getRole())
                .kycStatus(entity.getKycStatus())
                .build();
    }
}
