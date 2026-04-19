package com.mybank.transaction.infrastructure.adapter.in.web;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.infrastructure.adapter.in.web.dto.ApiResponse;
import com.mybank.transaction.infrastructure.adapter.in.web.dto.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
}
