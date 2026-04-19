package com.mybank.transaction.infrastructure.adapter.in.web;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.domain.model.TransactionStatus;
import com.mybank.transaction.domain.model.TransactionType;
import com.mybank.transaction.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.transaction.infrastructure.adapter.in.web.dto.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for handling transfers")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    @PostMapping("/transfer")
    @Operation(summary = "Initiate Transfer", description = "Initiates a fund transfer securely.")
    public ResponseEntity<ApiResponse<Transaction>> initiateTransfer(
            @Valid @RequestBody TransferRequest request,
            JwtAuthenticationToken authentication) {

        String userIdStr = (String) authentication.getTokenAttributes().get("userId");
        UUID authenticatedUserId = UUID.fromString(userIdStr);

        Transaction transaction = transactionUseCase.initiateTransfer(
                authenticatedUserId,
                request.getSenderId(),
                request.getReceiverId(),
                request.getAmount()
        );
        ApiResponse<Transaction> response = ApiResponse.<Transaction>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Transfer initiated successfully")
                .data(transaction)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{accountId}")
    @Operation(summary = "Get Transaction History", description = "Retrieves transaction statement for an account with optional filters.")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionHistory(
            @PathVariable UUID accountId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            JwtAuthenticationToken authentication) {

        String userIdStr = (String) authentication.getTokenAttributes().get("userId");
        UUID authenticatedUserId = UUID.fromString(userIdStr);

        List<Transaction> transactions = transactionUseCase.getTransactionHistory(
                authenticatedUserId, accountId, minAmount, maxAmount, type, startDate, endDate
        );

        ApiResponse<List<Transaction>> response = ApiResponse.<List<Transaction>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Transaction history retrieved successfully")
                .data(transactions)
                .build();
        return ResponseEntity.ok(response);
    }

    // --- Management APIs ---

    @GetMapping("/management/transactions")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Global Transaction Monitoring", description = "Monitor all transactions across the system with advanced filters. Manager only.")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<Transaction> transactions = transactionUseCase.getAllTransactions(
                accountId, minAmount, maxAmount, type, status, startDate, endDate
        );

        ApiResponse<List<Transaction>> response = ApiResponse.<List<Transaction>>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message("Global transactions retrieved successfully")
                .data(transactions)
                .build();
        return ResponseEntity.ok(response);
    }
}
