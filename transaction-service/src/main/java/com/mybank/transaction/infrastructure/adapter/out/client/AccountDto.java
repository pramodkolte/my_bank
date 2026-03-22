package com.mybank.transaction.infrastructure.adapter.out.client;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private UUID ownerId;
    private BigDecimal balance;
    private String status;
}
