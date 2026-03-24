package com.mybank.identity.infrastructure.adapter.out.persistence;

import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KYCStatus kycStatus;
}
