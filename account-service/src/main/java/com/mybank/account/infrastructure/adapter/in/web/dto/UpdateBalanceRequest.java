package com.mybank.account.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateBalanceRequest {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
