package com.mybank.account.infrastructure.adapter.in.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountUpdateRequest {
    private String status;
    private BigDecimal balance;
}
