package com.mybank.identity.application.service;

import com.mybank.identity.application.exception.InvalidCredentialsException;
import com.mybank.identity.application.exception.UserAlreadyExistsException;
import com.mybank.identity.application.port.in.AuthUseCase;
import com.mybank.identity.application.port.out.JwtProviderPort;
import com.mybank.identity.application.port.out.PasswordEncoderPort;
import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import com.mybank.identity.domain.model.User;
import com.mybank.identity.domain.port.out.EventPublisherPort;
import com.mybank.identity.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtProviderPort jwtProviderPort;
    private final EventPublisherPort eventPublisherPort;

    @Override
    public User register(String email, String rawPassword, String roleStr) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists.");
        }
        
        Role role = Role.valueOf(roleStr.toUpperCase());
        String encodedPassword = passwordEncoderPort.encode(rawPassword);
        
        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(encodedPassword)
                .role(role)
                .kycStatus(KYCStatus.PENDING)
                .build();
                
        User savedUser = userRepositoryPort.save(user);
        eventPublisherPort.publishUserRegisteredEvent(savedUser);
        return savedUser;
    }

    @Override
    public String login(String email, String rawPassword) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
                
        if (!passwordEncoderPort.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        return jwtProviderPort.generateToken(user);
    }
    
    @Override
    public void updateKycStatus(String email, String statusStr) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
                
        user.setKycStatus(KYCStatus.valueOf(statusStr.toUpperCase()));
        User updated = userRepositoryPort.save(user);
        eventPublisherPort.publishKycStatusUpdatedEvent(updated);
    }
}
