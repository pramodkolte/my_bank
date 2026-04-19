package com.mybank.transaction.infrastructure.adapter.out.client;

import com.mybank.transaction.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.transaction.infrastructure.config.FeignConfig;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service", configuration = FeignConfig.class)
public interface IdentityServiceClient {

    @GetMapping("/api/v1/identity/users/{id}/verify")
    @CircuitBreaker(name = "identityService")
    @Retry(name = "identityService")
    ApiResponse<Boolean> verifyUser(@PathVariable("id") UUID id);
}
