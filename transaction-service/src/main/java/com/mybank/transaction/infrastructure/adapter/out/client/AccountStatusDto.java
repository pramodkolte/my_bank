package com.mybank.transaction.infrastructure.adapter.out.client;

import lombok.Data;
import java.util.UUID;

@Data
public class AccountStatusDto {
    private UUID id;
    private UUID ownerId;
    private String status;
}
