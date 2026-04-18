package com.mybank.transaction.infrastructure.adapter.in.web;

import com.mybank.transaction.application.port.in.TransactionUseCase;
import com.mybank.transaction.domain.model.Transaction;
import com.mybank.transaction.infrastructure.adapter.in.web.dto.TransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for handling transfers")
public class TransactionController {

    private final TransactionUseCase transactionUseCase;

    @PostMapping("/transfer")
    // Restricts execution to tokens whose userId claim specifically matches the embedded senderId!
    @PreAuthorize("authentication.tokenAttributes['userId'] == #request.senderId.toString()")
    @Operation(summary = "Initiate Transfer", description = "Initiates a fund transfer securely.")
    public ResponseEntity<Transaction> initiateTransfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transactionUseCase.initiateTransfer(
                request.getSenderId(),
                request.getReceiverId(),
                request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }
}
