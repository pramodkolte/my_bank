package com.mybank.account.infrastructure.adapter.in.web;

import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.domain.model.Account;
import com.mybank.account.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.account.infrastructure.adapter.in.web.dto.AccountStatusResponse;
import com.mybank.account.infrastructure.adapter.in.web.dto.CreateAccountRequest;
import com.mybank.account.infrastructure.adapter.in.web.dto.UpdateBalanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Endpoints for Account Management")
public class AccountController {

    private final AccountUseCase accountUseCase;

    @GetMapping("/{accountId}")
    @Operation(summary = "Get Account", description = "Retrieves an account by ID")
    @PostAuthorize("returnObject.body.data.ownerId.toString() == authentication.tokenAttributes['userId'] or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Account>> getAccount(@PathVariable UUID accountId) {
        Account account = accountUseCase.getAccount(accountId);
        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account details retrieved")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/status")
    @Operation(summary = "Get Account Status", description = "Retrieves minimal account status for verification. Accessible by any authenticated user.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AccountStatusResponse>> getAccountStatus(@PathVariable UUID accountId) {
        Account account = accountUseCase.getAccount(accountId);
        AccountStatusResponse statusResponse = AccountStatusResponse.builder()
                .id(account.getId())
                .ownerId(account.getOwnerId())
                .status(account.getStatus().name())
                .build();
        
        ApiResponse<AccountStatusResponse> response = ApiResponse.<AccountStatusResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account status retrieved")
                .data(statusResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create an Account", description = "Creates a new account after verifying the identity via Feign client.")
    @PreAuthorize("#request.ownerId.toString() == authentication.tokenAttributes['userId'] or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Account>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountUseCase.createAccount(request.getOwnerId(), request.getCurrency());
        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account created successfully")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/balance")
    @Operation(summary = "Update Balance", description = "Updates an account balance ensuring thread-safety with Pessimistic Locking.")
    @PreAuthorize("hasRole('MANAGER')") // Restrict direct balance updates to Managers (e.g. system usage)
    public ResponseEntity<ApiResponse<Account>> updateBalance(@PathVariable UUID accountId, @Valid @RequestBody UpdateBalanceRequest request) {
        Account account = accountUseCase.updateBalance(accountId, request.getAmount());
        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account balance updated")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }
}
