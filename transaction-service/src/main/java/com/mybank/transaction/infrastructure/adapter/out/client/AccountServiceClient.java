package com.mybank.transaction.infrastructure.adapter.out.client;

import com.mybank.transaction.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.transaction.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service", configuration = FeignConfig.class)
public interface AccountServiceClient {
    
    @GetMapping("/api/v1/accounts/{id}")
    ApiResponse<AccountDto> getAccount(@PathVariable("id") UUID id);
}
