package com.mybank.identity.infrastructure.adapter.out.persistence;

import com.mybank.identity.domain.model.KYCStatus;
import com.mybank.identity.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("identity_test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private SpringDataUserRepository repository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        UserEntity entity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("test@mybank.com")
                .passwordHash("hashedPwd")
                .role(Role.CUSTOMER)
                .kycStatus(KYCStatus.PENDING)
                .build();

        // When
        repository.save(entity);
        Optional<UserEntity> retrieved = repository.findByEmail("test@mybank.com");

        // Then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("test@mybank.com");
        assertThat(retrieved.get().getRole()).isEqualTo(Role.CUSTOMER);
    }
}
