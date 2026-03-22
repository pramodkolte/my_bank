package com.mybank.transaction.infrastructure.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
    
    @GetMapping("/api/v1/accounts/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);
}
