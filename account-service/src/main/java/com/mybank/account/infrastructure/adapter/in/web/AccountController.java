package com.mybank.account.infrastructure.adapter.in.web;

import com.mybank.account.application.port.in.AccountUseCase;
import com.mybank.account.domain.model.Account;
import com.mybank.account.infrastructure.adapter.in.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Accounts", description = "Retrieves all accounts for a specific user ID.")
    @PreAuthorize("#userId.toString() == authentication.tokenAttributes['userId'] or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<Account>>> getAccountsByUser(@PathVariable UUID userId) {
        List<Account> accounts = accountUseCase.getAccountsByOwner(userId);
        ApiResponse<List<Account>> response = ApiResponse.<List<Account>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("User accounts retrieved successfully")
                .data(accounts)
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

    // --- Management APIs ---

    @GetMapping("/management/accounts")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "List All Accounts", description = "Monitor accounts with advanced filters. Manager only.")
    public ResponseEntity<ApiResponse<List<Account>>> getAllAccounts(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {
        List<Account> accounts = accountUseCase.getAccounts(ownerId, currency, status, minBalance, maxBalance);
        ApiResponse<List<Account>> response = ApiResponse.<List<Account>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Accounts retrieved successfully")
                .data(accounts)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/management/accounts/{accountId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get Any Account", description = "View detailed account balance and status. Manager only.")
    public ResponseEntity<ApiResponse<Account>> getAnyAccount(@PathVariable UUID accountId) {
        Account account = accountUseCase.getAccount(accountId);
        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account details retrieved")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/management/accounts/{accountId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update Account", description = "Manually adjust account balance or override status. Manager only.")
    public ResponseEntity<ApiResponse<Account>> updateAccount(@PathVariable UUID accountId, @Valid @RequestBody AccountUpdateRequest request) {
        Account account = accountUseCase.updateAccount(accountId, request.getStatus(), request.getBalance());
        ApiResponse<Account> response = ApiResponse.<Account>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account updated successfully")
                .data(account)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/management/accounts/{accountId}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete Account", description = "Close and remove an account from the system. Manager only.")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable UUID accountId) {
        accountUseCase.deleteAccount(accountId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Account deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
