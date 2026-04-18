package com.mybank.transaction.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {
    @NotNull(message = "Sender ID is required")
    private UUID senderId;

    @NotNull(message = "Receiver ID is required")
    private UUID receiverId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
}
