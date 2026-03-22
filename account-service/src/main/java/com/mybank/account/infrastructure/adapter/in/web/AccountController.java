package com.mybank.account.infrastructure.adapter.in.web;

import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.domain.model.Account;
import com.mybank.account.infrastructure.adapter.in.web.dto.CreateAccountRequest;
import com.mybank.account.infrastructure.adapter.in.web.dto.UpdateBalanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Endpoints for Account Management")
public class AccountController {

    private final AccountUseCase accountUseCase;

    @GetMapping("/{accountId}")
    @Operation(summary = "Get Account", description = "Retrieves an account by ID")
    public ResponseEntity<Account> getAccount(@PathVariable UUID accountId) {
        Account account = accountUseCase.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PostMapping
    @Operation(summary = "Create an Account", description = "Creates a new account after verifying the identity via Feign client.")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountUseCase.createAccount(request.getOwnerId(), request.getCurrency());
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{accountId}/balance")
    @Operation(summary = "Update Balance", description = "Updates an account balance ensuring thread-safety with Pessimistic Locking.")
    public ResponseEntity<Account> updateBalance(@PathVariable UUID accountId, @RequestBody UpdateBalanceRequest request) {
        Account account = accountUseCase.updateBalance(accountId, request.getAmount());
        return ResponseEntity.ok(account);
    }
}
