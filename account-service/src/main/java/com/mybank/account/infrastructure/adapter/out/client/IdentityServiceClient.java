package com.mybank.account.infrastructure.adapter.out.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

    @GetMapping("/api/v1/identity/users/{id}/verify")
    @CircuitBreaker(name = "identityService", fallbackMethod = "verifyUserExistsFallback")
    Boolean verifyUserExists(@PathVariable("id") UUID id);

    default Boolean verifyUserExistsFallback(UUID id, Throwable t) {
        throw new com.mybank.account.application.exception.ServiceUnavailableException("Identity Service is unreachable or failing. Cannot verify user.");
    }
}
