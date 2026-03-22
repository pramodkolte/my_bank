package com.mybank.account.infrastructure.adapter.in.web.dto;

import com.mybank.account.domain.model.Currency;
import lombok.Data;
import java.util.UUID;

@Data
public class CreateAccountRequest {
    private UUID ownerId;
    private Currency currency;
}
