package com.mybank.account.infrastructure.adapter.in.web.dto;

import com.mybank.account.domain.model.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateAccountRequest {
    @NotNull(message = "Owner ID is required")
    private UUID ownerId;
    
    @NotNull(message = "Currency is required")
    private Currency currency;
}
